package com.kh.spring.common.scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.service.BoardServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileDeleteScheduler {

	@Autowired
	private BoardService boardService;
	
	@Autowired
	private ServletContext application;
	
	// 매달 1일에 아래 함수 실행할 예정
	@Scheduled(cron = "0 0 0 1 * *")
	public void deleteFile() {
		log.info("deleteFile 실행");
		// 1) board테이블 안에있는 모든 파일 목록 조회.
		List<String> list = boardService.selectFileList();
//		List<String> list = new ArrayList();
//		list.add("2023080910491329673.png");
//		list.add("2023080914375877010.jpg");
//		list.add("2023080914165571951.jpg");
//		list.add("2023080914363570809.jpg");
//		list.add("2023080914454558039.jpg");
		
		// 2) resources/images/board/P OR P폴더들 아래에있는 모든 이미지 파일 목록 조회.
		
		File path = new File(application.getRealPath("/resources/images/board/P"));
		File[] files = path.listFiles();
		// path가 참조하고있는 폴더에 들어가있는 모든 파일을 얻어와서 file배열로 반환해주는 녀석.
		
		List<File> fileList =  Arrays.asList(files);
		
		if( !list.isEmpty()) {
			
			for( File serverFile   :   fileList ) {
				String fileName = serverFile.getName(); // 파일명 얻어오는 메서드.
				
				// List.indexOf(value) : List에 value과 같은값이 있으면 인덱스를 반환 / 없으면 -1을 반환 
				if(list.indexOf(fileName) == -1) {
					// select해온 db목록에는 없는데,... 실제 웹서버 상에는 저장된 파일인 경우
					
					log.info(fileName+"이 삭제되었습니다.");
					serverFile.delete();
				}
			}
		}
		
	}
	
	
}
