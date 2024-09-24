package cjkimhello97.toy.crashMyServer.chat.utils;

public class GroupChatMessageUtils {

    private static final String ENTER_MESSAGE = " 님이 입장하셨습니다!";
    private static final String LEAVE_MESSAGE = " 님이 퇴장하셨습니다!";

    public static String enterGroupChatRoomMessage(String senderNickname) {
        return senderNickname + ENTER_MESSAGE;
    }

    public static String leaveGroupChatRoomMessage(String senderNickname) {
        return senderNickname + LEAVE_MESSAGE;
    }
}
