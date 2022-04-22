# jarvis-data-maven-plugin

This maven plugin is used to publish a file to the targeted repository. This objective is to upload the plugin jar file and its zipped data folder to download them later.

Here are the parameters:

- **host**: repository address.
- **repositoryDnsServiceName**: name of the repository service on the network. This service will be resolved if the **host** property is not specified.
- **file**: The file to upload to the repository.
- **targetFilename**:  Name of the file on the repository (to rename it if necessary).
- **path**: HTTP path of the request to call on the repository in order to upload the targeted file. 

```xml
<build>
    <plugins>
        <!--  
            Plugin that uploads the jar & data files to the repository 
        -->
        <plugin>
            <groupId>com.gguproject.jarvis</groupId>
            <artifactId>jarvis-data-maven-plugin</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <executions>
                <!-- The first execution is for uploading data file-->
                <execution>
                    <id>execution-data</id>
                    <phase>install</phase>
                    <configuration>
                        <!-- 
                            The host is optional. If specified as the repository address, it will be used. 
                            Else it will look for the repository service using mdns and the name defined in the repositoryDnsServiceName property
                        -->
                        <host>localhost:8080</host>
                        <repositoryDnsServiceName>jarvis.com.gguproject.jarvis:repository_v0.0.1._http.local.</repositoryDnsServiceName>
                        <!-- Which file has to be uploaded (here the data zip file) -->
                        <file>${basedir}/target/data-${output.name}-${project.version}.zip</file>
                        <!-- And what is its target name on the repository -->
                        <targetFilename>data-${output.name}-${project.version}.zip</targetFilename>
                        <!-- Which API has to be called to upload it  -->
                        <path>/jar/${output.name}/version/${project.version}/data</path>
                    </configuration>
                    <goals>
                        <goal>repository-upload</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```