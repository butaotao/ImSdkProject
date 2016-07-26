package com.dachen.imsdk.entity;


import com.dachen.imsdk.db.po.ChatMessagePo;

import java.util.List;
import java.util.Map;

public class ChatMessageV2 {

    public String groupId;
    public long ts;
    public boolean more;
    public List<ChatMessagePo> msgList;

    //	public static class ChatMessageContent{
//		public String msgId;
//		public int type;
//		public String clientMsgId;
//		public String fromUserId;
//		public long sendTime;
//		public String content;
//		public int status;
//		public int direction;
//		public String groupId;
//		public String sourceId;
//		public String fromClient;
//		public String param;
//	}
    public static class FileMsgBaseParam {
        public String uri;
        public String key;
        public boolean isPic(){
            return  false;
        }
    }

    public static class ImageMsgParam extends FileMsgBaseParam {
        public String name;
        public String size;
        public int width;
        public int height;
        @Override
        public boolean isPic() {
            return true;
        }
    }

    public static class VoiceMsgParam extends FileMsgBaseParam {
        public String name;
        public String size;
        public String time;
    }

    public static class ArchiveMsgParam extends FileMsgBaseParam {
        public String name;
        public String size;
        public String format;
    }

    public static class TextAndUriMsgParam {
        public String uri;
        public Map<String,Object> bizParam;
    }

    public static class AtMsgParam{
        public int notify_type;
        public boolean notify_to_all;
        public List<String> notify_to;
    }
}
