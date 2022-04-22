package com.gguproject.jarvis.plugin.androidtv.encoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.protobuf.Internal;

public final class Key {
  private Key() {}
  
  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite paramExtensionRegistryLite) {}
  
  public static void internalForceInit() {}
  
  public enum Code implements com.google.protobuf.Internal.EnumLite {
	KEYCODE_UNKNOWN(0, 0), 
    KEYCODE_SOFT_LEFT(1, 1), 
    KEYCODE_SOFT_RIGHT(2, 2), 
    KEYCODE_HOME(3, 3), 
    KEYCODE_BACK(4, 4), 
    KEYCODE_CALL(5, 5), 
    KEYCODE_0(6, 7), 
    KEYCODE_1(7, 8), 
    KEYCODE_2(8, 9), 
    KEYCODE_3(9, 10), 
    KEYCODE_4(10, 11), 
    KEYCODE_5(11, 12), 
    KEYCODE_6(12, 13), 
    KEYCODE_7(13, 14), 
    KEYCODE_8(14, 15), 
    KEYCODE_9(15, 16), 
    KEYCODE_STAR(16, 17), 
    KEYCODE_POUND(17, 18), 
    KEYCODE_DPAD_UP(18, 19), 
    KEYCODE_DPAD_DOWN(19, 20), 
    KEYCODE_DPAD_LEFT(20, 21), 
    KEYCODE_DPAD_RIGHT(21, 22), 
    KEYCODE_DPAD_CENTER(22, 23), 
    KEYCODE_VOLUME_UP(23, 24), 
    KEYCODE_VOLUME_DOWN(24, 25), 
    KEYCODE_POWER(25, 26), 
    KEYCODE_CAMERA(26, 27), 
    KEYCODE_A(27, 29), 
    KEYCODE_B(28, 30), 
    KEYCODE_C(29, 31), 
    KEYCODE_D(30, 32), 
    KEYCODE_E(31, 33), 
    KEYCODE_F(32, 34), 
    KEYCODE_G(33, 35), 
    KEYCODE_H(34, 36), 
    KEYCODE_I(35, 37), 
    KEYCODE_J(36, 38), 
    KEYCODE_K(37, 39), 
    KEYCODE_L(38, 40), 
    KEYCODE_M(39, 41), 
    KEYCODE_N(40, 42), 
    KEYCODE_O(41, 43), 
    KEYCODE_P(42, 44), 
    KEYCODE_Q(43, 45), 
    KEYCODE_R(44, 46), 
    KEYCODE_S(45, 47), 
    KEYCODE_T(46, 48), 
    KEYCODE_U(47, 49), 
    KEYCODE_V(48, 50), 
    KEYCODE_W(49, 51), 
    KEYCODE_X(50, 52), 
    KEYCODE_Y(51, 53), 
    KEYCODE_Z(52, 54), 
    KEYCODE_COMMA(53, 55), 
    KEYCODE_PERIOD(54, 56), 
    KEYCODE_ALT_LEFT(55, 57), 
    KEYCODE_ALT_RIGHT(56, 58), 
    KEYCODE_SHIFT_LEFT(57, 59), 
    KEYCODE_SHIFT_RIGHT(58, 60), 
    KEYCODE_TAB(59, 61), 
    KEYCODE_SPACE(60, 62), 
    KEYCODE_EXPLORER(61, 64), 
    KEYCODE_ENTER(62, 66), 
    KEYCODE_DEL(63, 67), 
    KEYCODE_GRAVE(64, 68), 
    KEYCODE_MINUS(65, 69), 
    KEYCODE_EQUALS(66, 70), 
    KEYCODE_LEFT_BRACKET(67, 71), 
    KEYCODE_RIGHT_BRACKET(68, 72), 
    KEYCODE_BACKSLASH(69, 73), 
    KEYCODE_SEMICOLON(70, 74), 
    KEYCODE_APOSTROPHE(71, 75), 
    KEYCODE_SLASH(72, 76), 
    KEYCODE_AT(73, 77), 
    KEYCODE_FOCUS(74, 80), 
    KEYCODE_PLUS(75, 81), 
    KEYCODE_MENU(76, 82), 
    KEYCODE_SEARCH(77, 84), 
    KEYCODE_MEDIA_PLAY_PAUSE(78, 85), 
    KEYCODE_MEDIA_STOP(79, 86), 
    KEYCODE_MEDIA_NEXT(80, 87), 
    KEYCODE_MEDIA_PREVIOUS(81, 88), 
    KEYCODE_MEDIA_REWIND(82, 89), 
    KEYCODE_MEDIA_FAST_FORWARD(83, 90), 
    KEYCODE_MUTE(84, 91), 
    KEYCODE_CTRL_LEFT(85, 92), 
    KEYCODE_CTRL_RIGHT(86, 93), 
    KEYCODE_INSERT(87, 94), 
    KEYCODE_PAUSE(88, 95), 
    KEYCODE_PAGE_UP(89, 96), 
    KEYCODE_PAGE_DOWN(90, 97), 
    KEYCODE_PRINT_SCREEN(91, 98), 
    KEYCODE_INFO(92, 103), 
    KEYCODE_WINDOW(93, 104), 
    KEYCODE_BOOKMARK(94, 110), 
    KEYCODE_CAPS_LOCK(95, 111), 
    KEYCODE_ESCAPE(96, 112), 
    KEYCODE_META_LEFT(97, 113), 
    KEYCODE_META_RIGHT(98, 114), 
    KEYCODE_ZOOM_IN(99, 115), 
    KEYCODE_ZOOM_OUT(100, 116), 
    KEYCODE_CHANNEL_UP(101, 117), 
    KEYCODE_CHANNEL_DOWN(102, 118), 
    KEYCODE_LIVE(103, 120), 
    KEYCODE_DVR(104, 121), 
    KEYCODE_GUIDE(105, 122), 
    KEYCODE_MEDIA_SKIP_BACK(106, 123), 
    KEYCODE_MEDIA_SKIP_FORWARD(107, 124), 
    KEYCODE_MEDIA_RECORD(108, 125), 
    KEYCODE_MEDIA_PLAY(109, 126), 
    KEYCODE_PROG_RED(110, 128), 
    KEYCODE_PROG_GREEN(111, 129), 
    KEYCODE_PROG_YELLOW(112, 130), 
    KEYCODE_PROG_BLUE(113, 131), 
    KEYCODE_BD_POWER(114, 132), 
    KEYCODE_BD_INPUT(115, 133), 
    KEYCODE_STB_POWER(116, 134), 
    KEYCODE_STB_INPUT(117, 135), 
    KEYCODE_STB_MENU(118, 136), 
    KEYCODE_TV_POWER(119, 137), 
    KEYCODE_TV_INPUT(120, 138), 
    KEYCODE_AVR_POWER(121, 139), 
    KEYCODE_AVR_INPUT(122, 140), 
    KEYCODE_AUDIO(123, 141), 
    KEYCODE_EJECT(124, 142), 
    KEYCODE_BD_POPUP_MENU(125, 143), 
    KEYCODE_BD_TOP_MENU(126, 144), 
    KEYCODE_SETTINGS(127, 145), 
    KEYCODE_SETUP(128, 146), 
    BTN_FIRST(129, 256), 
    BTN_1(132, 257), 
    BTN_2(133, 258), 
    BTN_3(134, 259), 
    BTN_4(135, 260), 
    BTN_5(136, 261), 
    BTN_6(137, 262), 
    BTN_7(138, 263), 
    BTN_8(139, 264), 
    BTN_9(140, 265), 
    BTN_MOUSE(141, 272), 
    BTN_RIGHT(143, 273), 
    BTN_MIDDLE(144, 274), 
    BTN_SIDE(145, 275), 
    BTN_EXTRA(146, 276), 
    BTN_FORWARD(147, 277), 
    BTN_BACK(148, 278), 
    BTN_TASK(149, 279);
    
  /**
   * Parse a channel to a list of codes
   * @param channel
   * @return
   */
	public static List<Code> parseChannel(int channel) {
		List<Code> codes = new ArrayList<>();
		for(char c : String.valueOf(channel).toCharArray()) {
			switch (c) {
				case '0': codes.add(Code.KEYCODE_0); break;
				case '1': codes.add(Code.KEYCODE_1); break;
				case '2': codes.add(Code.KEYCODE_2); break;
				case '3': codes.add(Code.KEYCODE_3); break;
				case '4': codes.add(Code.KEYCODE_4); break;
				case '5': codes.add(Code.KEYCODE_5); break;
				case '6': codes.add(Code.KEYCODE_6); break;
				case '7': codes.add(Code.KEYCODE_7); break;
				case '8': codes.add(Code.KEYCODE_8); break;
				case '9': codes.add(Code.KEYCODE_9); break;
			}
		}
		return codes;
	}
	  

    public static final Code BTN_MISC = BTN_FIRST;
    public static final Code BTN_0 = BTN_FIRST;
    public static final Code BTN_LEFT = BTN_MOUSE;
    
    public final int getNumber() { return value; }
    
    public static Code valueOf(int paramInt) {
      switch (paramInt) {
      case 0:  return KEYCODE_UNKNOWN;
      case 1:  return KEYCODE_SOFT_LEFT;
      case 2:  return KEYCODE_SOFT_RIGHT;
      case 3:  return KEYCODE_HOME;
      case 4:  return KEYCODE_BACK;
      case 5:  return KEYCODE_CALL;
      case 7:  return KEYCODE_0;
      case 8:  return KEYCODE_1;
      case 9:  return KEYCODE_2;
      case 10:  return KEYCODE_3;
      case 11:  return KEYCODE_4;
      case 12:  return KEYCODE_5;
      case 13:  return KEYCODE_6;
      case 14:  return KEYCODE_7;
      case 15:  return KEYCODE_8;
      case 16:  return KEYCODE_9;
      case 17:  return KEYCODE_STAR;
      case 18:  return KEYCODE_POUND;
      case 19:  return KEYCODE_DPAD_UP;
      case 20:  return KEYCODE_DPAD_DOWN;
      case 21:  return KEYCODE_DPAD_LEFT;
      case 22:  return KEYCODE_DPAD_RIGHT;
      case 23:  return KEYCODE_DPAD_CENTER;
      case 24:  return KEYCODE_VOLUME_UP;
      case 25:  return KEYCODE_VOLUME_DOWN;
      case 26:  return KEYCODE_POWER;
      case 27:  return KEYCODE_CAMERA;
      case 29:  return KEYCODE_A;
      case 30:  return KEYCODE_B;
      case 31:  return KEYCODE_C;
      case 32:  return KEYCODE_D;
      case 33:  return KEYCODE_E;
      case 34:  return KEYCODE_F;
      case 35:  return KEYCODE_G;
      case 36:  return KEYCODE_H;
      case 37:  return KEYCODE_I;
      case 38:  return KEYCODE_J;
      case 39:  return KEYCODE_K;
      case 40:  return KEYCODE_L;
      case 41:  return KEYCODE_M;
      case 42:  return KEYCODE_N;
      case 43:  return KEYCODE_O;
      case 44:  return KEYCODE_P;
      case 45:  return KEYCODE_Q;
      case 46:  return KEYCODE_R;
      case 47:  return KEYCODE_S;
      case 48:  return KEYCODE_T;
      case 49:  return KEYCODE_U;
      case 50:  return KEYCODE_V;
      case 51:  return KEYCODE_W;
      case 52:  return KEYCODE_X;
      case 53:  return KEYCODE_Y;
      case 54:  return KEYCODE_Z;
      case 55:  return KEYCODE_COMMA;
      case 56:  return KEYCODE_PERIOD;
      case 57:  return KEYCODE_ALT_LEFT;
      case 58:  return KEYCODE_ALT_RIGHT;
      case 59:  return KEYCODE_SHIFT_LEFT;
      case 60:  return KEYCODE_SHIFT_RIGHT;
      case 61:  return KEYCODE_TAB;
      case 62:  return KEYCODE_SPACE;
      case 64:  return KEYCODE_EXPLORER;
      case 66:  return KEYCODE_ENTER;
      case 67:  return KEYCODE_DEL;
      case 68:  return KEYCODE_GRAVE;
      case 69:  return KEYCODE_MINUS;
      case 70:  return KEYCODE_EQUALS;
      case 71:  return KEYCODE_LEFT_BRACKET;
      case 72:  return KEYCODE_RIGHT_BRACKET;
      case 73:  return KEYCODE_BACKSLASH;
      case 74:  return KEYCODE_SEMICOLON;
      case 75:  return KEYCODE_APOSTROPHE;
      case 76:  return KEYCODE_SLASH;
      case 77:  return KEYCODE_AT;
      case 80:  return KEYCODE_FOCUS;
      case 81:  return KEYCODE_PLUS;
      case 82:  return KEYCODE_MENU;
      case 84:  return KEYCODE_SEARCH;
      case 85:  return KEYCODE_MEDIA_PLAY_PAUSE;
      case 86:  return KEYCODE_MEDIA_STOP;
      case 87:  return KEYCODE_MEDIA_NEXT;
      case 88:  return KEYCODE_MEDIA_PREVIOUS;
      case 89:  return KEYCODE_MEDIA_REWIND;
      case 90:  return KEYCODE_MEDIA_FAST_FORWARD;
      case 91:  return KEYCODE_MUTE;
      case 92:  return KEYCODE_CTRL_LEFT;
      case 93:  return KEYCODE_CTRL_RIGHT;
      case 94:  return KEYCODE_INSERT;
      case 95:  return KEYCODE_PAUSE;
      case 96:  return KEYCODE_PAGE_UP;
      case 97:  return KEYCODE_PAGE_DOWN;
      case 98:  return KEYCODE_PRINT_SCREEN;
      case 103:  return KEYCODE_INFO;
      case 104:  return KEYCODE_WINDOW;
      case 110:  return KEYCODE_BOOKMARK;
      case 111:  return KEYCODE_CAPS_LOCK;
      case 112:  return KEYCODE_ESCAPE;
      case 113:  return KEYCODE_META_LEFT;
      case 114:  return KEYCODE_META_RIGHT;
      case 115:  return KEYCODE_ZOOM_IN;
      case 116:  return KEYCODE_ZOOM_OUT;
      case 117:  return KEYCODE_CHANNEL_UP;
      case 118:  return KEYCODE_CHANNEL_DOWN;
      case 120:  return KEYCODE_LIVE;
      case 121:  return KEYCODE_DVR;
      case 122:  return KEYCODE_GUIDE;
      case 123:  return KEYCODE_MEDIA_SKIP_BACK;
      case 124:  return KEYCODE_MEDIA_SKIP_FORWARD;
      case 125:  return KEYCODE_MEDIA_RECORD;
      case 126:  return KEYCODE_MEDIA_PLAY;
      case 128:  return KEYCODE_PROG_RED;
      case 129:  return KEYCODE_PROG_GREEN;
      case 130:  return KEYCODE_PROG_YELLOW;
      case 131:  return KEYCODE_PROG_BLUE;
      case 132:  return KEYCODE_BD_POWER;
      case 133:  return KEYCODE_BD_INPUT;
      case 134:  return KEYCODE_STB_POWER;
      case 135:  return KEYCODE_STB_INPUT;
      case 136:  return KEYCODE_STB_MENU;
      case 137:  return KEYCODE_TV_POWER;
      case 138:  return KEYCODE_TV_INPUT;
      case 139:  return KEYCODE_AVR_POWER;
      case 140:  return KEYCODE_AVR_INPUT;
      case 141:  return KEYCODE_AUDIO;
      case 142:  return KEYCODE_EJECT;
      case 143:  return KEYCODE_BD_POPUP_MENU;
      case 144:  return KEYCODE_BD_TOP_MENU;
      case 145:  return KEYCODE_SETTINGS;
      case 146:  return KEYCODE_SETUP;
      case 256:  return BTN_FIRST;
      case 257:  return BTN_1;
      case 258:  return BTN_2;
      case 259:  return BTN_3;
      case 260:  return BTN_4;
      case 261:  return BTN_5;
      case 262:  return BTN_6;
      case 263:  return BTN_7;
      case 264:  return BTN_8;
      case 265:  return BTN_9;
      case 272:  return BTN_MOUSE;
      case 273:  return BTN_RIGHT;
      case 274:  return BTN_MIDDLE;
      case 275:  return BTN_SIDE;
      case 276:  return BTN_EXTRA;
      case 277:  return BTN_FORWARD;
      case 278:  return BTN_BACK;
      case 279:  return BTN_TASK; }
      return null;
    }
    

    public static Internal.EnumLiteMap<Code> internalGetValueMap() {
      return internalValueMap;
    }
    
    private static Internal.EnumLiteMap<Code> internalValueMap = new Internal.EnumLiteMap() {
      public Key.Code findValueByNumber(int paramAnonymousInt) {
        return Key.Code.valueOf(paramAnonymousInt);
      }
    };
    
    private final int index;
    private final int value;
    
    private Code(int paramInt1, int paramInt2) {
      index = paramInt1;
      value = paramInt2;
    }
  }
  
  public static enum Action implements com.google.protobuf.Internal.EnumLite {
    UP(0, 0), 
    DOWN(1, 1);

    public final int getNumber() { return value; }
    
    public static Action valueOf(int paramInt) {
      switch (paramInt) {
      case 0:  return UP;
      case 1:  return DOWN; }
      return null;
    }
    
    public static Internal.EnumLiteMap<Action> internalGetValueMap() {
      return internalValueMap;
    }
    
    private static Internal.EnumLiteMap<Action> internalValueMap = new Internal.EnumLiteMap() {
      public Key.Action findValueByNumber(int paramAnonymousInt) {
        return Key.Action.valueOf(paramAnonymousInt);
      }
    };
    
    private final int index;
    private final int value;
    
    private Action(int paramInt1, int paramInt2) {
      index = paramInt1;
      value = paramInt2;
    }
  }
}
