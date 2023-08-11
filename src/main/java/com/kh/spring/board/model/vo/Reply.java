package com.kh.spring.board.model.vo;

import lombok.Data;

@Data
public class Reply {
private int replyNo;//	REPLY_NO
private String replyContent;//	REPLY_CONTENT
private int refBno; //	REF_BNO
private String replyWriter;//	REPLY_WRITER
private String createDate;//	CREATE_DATE
private String status;//	STATUS
}
