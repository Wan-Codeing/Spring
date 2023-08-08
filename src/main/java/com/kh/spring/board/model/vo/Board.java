package com.kh.spring.board.model.vo;

import lombok.Data;

@Data
public class Board {
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardWriter;
	private int count;
	private String createDate;
	private String status;
	private String boardCd;
}
