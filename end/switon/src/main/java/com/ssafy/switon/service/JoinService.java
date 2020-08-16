package com.ssafy.switon.service;

import java.util.List;

import com.ssafy.switon.dto.Join;
import com.ssafy.switon.dto.UserSimpleDTO;

public interface JoinService {
	
	// 소모임 가입 정보 모두 조회
	List<Join> searchAll();
	// 소모임 가입 정보 상세 조회
	Join search(int id);
	// 소모임 가입 정보 등록
	boolean join(int studyId, int userId);
	// 소모임 가입(모임장)
	boolean createLeader(Join join);
	// 소모임 가입 정보 삭제
	boolean leave(int studyId, int userId);
	// 유저의 소모임 가입 정보 가져오기
	List<Join> searchAllByUserId(int userId);
	// 가입여부 반환
	boolean isMember(int studyId, int userId);
	// 소모임에 가입한 멤버들 리스트 반환
	List<UserSimpleDTO> searchMembers(int studyId);
	// 소모임에 인원수가 가득한지 검사
	boolean isFull(int studyId);
	// 특정 행동을 하면 이 메소드를 사용해 점수를 부여한다.
	boolean givePoint(int userId, int studyId, int point);
	
}
