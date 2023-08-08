package com.kh.spring.board.model.vo;

import java.sql.Date;

import lombok.Data;

@Data
public class Attachment {
	private int fileNo;
	private int refBno;
	private String originName;
	private String changeName;
	private String filePath;
	private Date uploadDate;
	private int fileLevel;
	private String status;
}
