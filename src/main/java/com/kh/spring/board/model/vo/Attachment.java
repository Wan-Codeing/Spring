package com.kh.spring.board.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class Attachment {
	private int fileNo;//	FILE_NO
	private int refBno;//	REF_BNO
	private String originName;//	ORIGIN_NAME
	private String changeName;//	CHANGE_NAME
	private String filePath;//	FILE_PATH
	private Date uploadDate;//	UPLOAD_DATE
	private int fileLevel;//	FILE_LEVEL
	private String status;//	STATUS
}
