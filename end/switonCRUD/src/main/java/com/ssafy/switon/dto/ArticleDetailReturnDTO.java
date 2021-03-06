package com.ssafy.switon.dto;

import java.sql.Timestamp;
import java.util.List;

import io.swagger.annotations.ApiParam;

public class ArticleDetailReturnDTO {
	
	@ApiParam(value = "게시글 id (PK)", required = false, hidden = true)
	int id;
	@ApiParam(value = "게시글 제목", required = true)
	String title;
	@ApiParam(value = "게시글 내용", required = true)
	String content;
	@ApiParam(value = "작성자 (토큰에서 긁어옴)", required = false, hidden = true)
	int user_id;
	@ApiParam(value = "첨부파일")
	String file;
	@ApiParam(value = "게시판 아이디 (스터디id에서 얻어냄)", required = false, hidden = true)
	int board_id;
	@ApiParam(value = "게시글 작성시간", required = false, hidden = true)
	Timestamp created_at;
	@ApiParam(value = "게시글 수정시간", required = false, hidden = true)
	Timestamp updated_at;
	// 간단한 스터디 정보
	StudySimple study;
	// 간단한 작성자 정보
	UserSimpleDTO user;
	// 댓글도 함께 반환해야 해서...
	List<CommentReturnDTO> comments;
	// 좋아요 상태도 함께 반환해야 해서...
	Like like;
	// 북마크 여부 반환
	boolean isfavorite;
	
	public ArticleDetailReturnDTO(Article article, List<CommentReturnDTO> comments, Like like) {
		this.id = article.getId();
		this.title = article.getTitle();
		this.content = article.getContent();
		this.user_id = article.getUser_id();
		this.file = article.getFile();
		this.board_id = article.getBoard_id();
		this.created_at = article.getCreated_at();
		this.updated_at = article.getUpdated_at();
		this.comments = comments;
		this.like = like;
	}
	
	public StudySimple getStudy() {
		return study;
	}

	public boolean isIsfavorite() {
		return isfavorite;
	}

	public void setIsfavorite(boolean isfavorite) {
		this.isfavorite = isfavorite;
	}

	public void setStudy(StudySimple study) {
		this.study = study;
	}



	public Like getLike() {
		return like;
	}

	public void setLike(Like like) {
		this.like = like;
	}

	public UserSimpleDTO getUser() {
		return user;
	}

	public void setUser(UserSimpleDTO user) {
		this.user = user;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public int getBoard_id() {
		return board_id;
	}
	public void setBoard_id(int board_id) {
		this.board_id = board_id;
	}
	public Timestamp getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}
	public Timestamp getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Timestamp updated_at) {
		this.updated_at = updated_at;
	}
	public List<CommentReturnDTO> getComments() {
		return comments;
	}
	public void setComments(List<CommentReturnDTO> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "ArticleDetailReturnDTO [id=" + id + ", title=" + title + ", content=" + content + ", user_id=" + user_id
				+ ", file=" + file + ", board_id=" + board_id + ", created_at=" + created_at + ", updated_at="
				+ updated_at + ", user=" + user + ", comments=" + comments + "]";
	}
	
}
