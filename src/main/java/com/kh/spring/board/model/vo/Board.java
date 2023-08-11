package com.kh.spring.board.model.vo;

import lombok.Data;

@Data
public class Board {
	private int boardNo; //	BOARD_NO
	private String boardTitle;//	BOARD_TITLE
	private String boardContent;//	BOARD_CONTENT
	private String boardWriter;//	BOARD_WRITER
	private int count;//	COUNT
	private String createDate;//	CREATE_DATE
	private String status;//	STATUS
	private String boardCd;//	BOARD_CD
	
}
