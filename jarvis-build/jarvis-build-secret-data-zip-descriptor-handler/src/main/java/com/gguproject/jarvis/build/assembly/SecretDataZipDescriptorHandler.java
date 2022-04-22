package com.gguproject.jarvis.build.assembly;

import org.apache.maven.plugins.assembly.filter.ContainerDescriptorHandler;
import org.apache.maven.plugins.assembly.utils.AssemblyFileUtils;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This descriptor handler is used to create the "secret-data" zip file
 * by renaming the root folder (which is "secret-data") by the plugin name.
 * The objective is to simplify the deployment of secret datas by copying it to
 * the targeted device and unzip it directly without having to rename it
 */
@Component(role = ContainerDescriptorHandler.class, hint = "secret-data-zip")
public class SecretDataZipDescriptorHandler extends AbstractLogEnabled implements ContainerDescriptorHandler {

    private String pluginName;

    private String root = "secret-data";

    /**
     * Secured file to process
     */
    private Map<String, FileInfo> securedFiles = new HashMap<>();

    /**
     * In the finalizeArchiveCreation method, when adding the new file to the archive using archiver.addFile
     * the isSelected will be called again. We need to skip the selection and let the archiver do its job
     * else it will end in an endless loop
     */
    private boolean excludeOverride = false;

    /**
     * This method returns true to let the assembly process continue processing the file
     * If false, the given file will be excluded from the processing
     * @param fileInfo
     * @return
     * @throws IOException
     */
    @Override
    public boolean isSelected(FileInfo fileInfo) throws IOException {
        if (excludeOverride) {
            return true;
        }

        String name = AssemblyFileUtils.normalizeFileInfo(fileInfo);
        securedFiles.put(name, fileInfo);
        return false;
    }

    @Override
    public void finalizeArchiveCreation(Archiver archiver) throws ArchiverException {
        this.getLogger().info("Replace root: '" + this.root + "' by: " + this.pluginName);
        archiver.getResources().forEachRemaining(a -> {}); // necessary to prompt the isSelected() call

        // for each file of the archive, create a temporary file to add to the archive
        // and rename their names so the root folder is the plugin name
        securedFiles.forEach((fileName, fileInfo) -> {
            if(fileInfo.isFile()) {
                try {
                    Path p = Files.createTempFile("tmp-assembly-" + new File(fileName).getName(), ".tmp");
                    Files.copy(fileInfo.getContents(), p, StandardCopyOption.REPLACE_EXISTING);

                    File file = p.toFile();
                    file.deleteOnExit();

                    excludeOverride = true;
                    archiver.addFile(file, fileName.replace(this.root + "/", this.pluginName + "/"));
                    excludeOverride = false;
                } catch (IOException e) {
                    this.getLogger().error("Not able to produce secret data zip", e);
                }
            }
        });

        securedFiles.clear();
    }

    @Override
    public void finalizeArchiveExtraction(UnArchiver unarchiver) throws ArchiverException { }

    @Override
    public List<String> getVirtualFiles() {
        return new ArrayList<>(securedFiles.keySet());
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public void setRoot(String root){
        this.root = root;
    }
}
