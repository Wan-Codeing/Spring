package com.kh.spring.common.scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.service.BoardServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileDeleteScheduler {
	
	public static void main(String[] args) {
		
		BoardService boardService = new BoardServiceImpl();
		
		// 1) board테이블에 있는 모든파일 목록 조회
		//List<String> list = boardService.selectFileList();
		List<String> list = new ArrayList();
		list.add("20230807150614163440.png");
		list.add("20230807153511124579.png");
		list.add("20230807154826133712.png");
		list.add("20230807154826180921.png");
		list.add("20230807154826132555.png");
		list.add("20230807154826142503.png");
		list.add("20230809094255131788.jfif");
		list.add("20230809102007116927.jfif");
		list.add("20230809102008165129.jfif");
		list.add("20230809144612176633.jfif");
		list.add("20230809143649152697.jfif");
		list.add("20230809143649114871.jfif");
		list.add("20230809144638138100.jfif");
		
		// 2) resources/imges/board/C OR P 폴더 아래에 있는 모든 이미지파일 목록 조회
		File path = new File("C:\\Users\\wan94\\OneDrive\\Desktop\\Spring-WorkSpace\\spring\\src\\main\\webapp\\resources\\images\\board\\P");
		File[] files = path.listFiles();
		// path가 참조하고있는 폴더에 들어가있는 모든파일을 얻어와서 file배열로 반환해주는 녀석
		
		List<File> fileList = Arrays.asList(files);
		
		if(!list.isEmpty()) {
			for( File serverFile : fileList ) {
				String fileName = serverFile.getName(); // 파일명 얻어오는 메서드
				if(list.indexOf(fileName) == -1) {
					// select해온 db목록에 없는데, 실제 웹서버상에는 저장된 파일인경우
					log.info(fileName+"이 삭제되었습니다.");
					serverFile.delete();
				}
				// List.indexOf(value) : 리스트에 value값과 같은값이 있으면 인덱스 반환 없으면 -1 반환
			}
		}
		
	}
}
scheduler-context.xml
