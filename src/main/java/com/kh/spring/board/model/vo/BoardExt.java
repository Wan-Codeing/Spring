package com.kh.spring.board.model.vo;

import java.util.List;

import lombok.Data;

@Data
public class BoardExt extends Board{
	
	private List<Attachment> attachList;
	private List<Reply> replyList;
}
