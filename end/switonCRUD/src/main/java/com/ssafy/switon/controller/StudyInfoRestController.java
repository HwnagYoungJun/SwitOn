﻿package com.ssafy.switon.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.switon.dao.StudyInfoDAO;
import com.ssafy.switon.dto.StudyInfo;

import io.swagger.annotations.ApiOperation;

@RestController
public class StudyInfoRestController {

	@Autowired
	StudyInfoDAO studyinfoDAO;
	// 인기 스터디 추천 알고리즘
	@ApiOperation(value="추천 스터디목록 반환", response = List.class)
	@GetMapping("/popularstudy")
	public List<StudyInfo> searchStudyInfo() {
		System.out.println("추천 스터디목록 반환");
		
		List<StudyInfo> list = studyinfoDAO.selectStudyInfo();

		
		for(int i = 0; i < list.size(); i++) {
			list.get(i).setValue(list.get(i).getUser(), list.get(i).getLike(), list.get(i).getSchedule(), list.get(i).getArticles());
		}

		Collections.sort(list, new Comparator<StudyInfo>() {
			
			@Override
			public int compare(StudyInfo o1, StudyInfo o2) {
				return o2.getValue() - o1.getValue();
			}
		});
		
		int a[] = new int[4];
		Random r = new Random();
		
		for(int i = 0; i < 4; i++) {
			a[i] = r.nextInt(10)+1;
			for(int j = 0;j<i;j++) {
				if(a[i] == a[j]) {
					i--;
				}
			}
		}
		
		List<StudyInfo> lastList = new ArrayList<StudyInfo>();
		
		for(int i = 0; i< 4; i++) {
			lastList.add(list.get(a[i]));
		}
		return lastList;
	}
}
