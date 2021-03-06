<template>
	<section v-if="article">
		<nav aria-label="Breadcrumb" class="breadcrumb">
			<ol>
				<li>
					<router-link :to="`/study/${article.study.id}`" tabindex="-1">{{
						article.study.name
					}}</router-link
					><span aria-hidden="true">></span>
				</li>
				<li>
					<router-link
						:to="{ name: `${board_name}` }"
						aria-current="page"
						tabindex="-1"
						>{{ board_name }}</router-link
					>
				</li>
			</ol>
		</nav>
		<section class="card-detail-wrap">
			<article class="card-detail">
				<header class="card-detail-title">
					<p tabindex="0">{{ article.title }}</p>
				</header>
				<div class="content-info">
					<router-link :to="`/profile/${article.user.name}`">
						<img :src="writerImg" alt="작성자이미지" />{{
							article.user.name
						}}</router-link
					>
					<span>{{ article.created_at | formatDate }}</span>
				</div>
				<section class="card-detail-content">
					<Viewer :initialValue="article.content" tabindex="0" />
					<div class="card-detail-filebox" v-if="article.file">
						<span class="card-detail-file" tabindex="0">첨부파일</span>
						<a
							v-if="FileCheck"
							:href="`${BaseUrl}${article.file}`"
							target="_blank"
							>{{ article.file | fileDownload }}</a
						>
						<a v-else :href="`${BaseUrl}${article.file}`" download>{{
							article.file | fileDownload
						}}</a>
					</div>
					<div class="logo" v-if="board_name !== 'notice'">
						<div class="logo-likebox">
							<i
								@click="articleUnLike"
								v-if="isLiked"
								class="icon ion-md-heart like"
								aria-label="좋아요 취소"
								tabindex="0"
							></i>
							<i
								@click="articleLike"
								v-else
								class="icon ion-md-heart unlike"
								aria-label="좋아요"
								tabindex="0"
							></i>
							<span tabindex="0">좋아요 {{ likeCount }}개</span>
						</div>
						<div class="logo-btnbox">
							<div class="bookmark" v-if="board_name === 'repository'">
								<i
									v-if="isBookmarked"
									@click="removeBookmark"
									class="icon ion-md-bookmark bookmark"
									aria-label="북마크 취소"
								></i>
								<i
									v-else
									@click="addBookmark"
									class="icon ion-md-bookmark unlike"
									aria-label="북마크"
								></i>
							</div>
							<span
								v-if="getName === article.user.name"
								class="span-btn"
								@click="editRouter"
								>수정</span
							>
							<span
								v-if="getName === article.user.name"
								class="span-btn"
								@click="removeArticle"
								>삭제</span
							>
						</div>
					</div>
					<div class="comment-form">
						<input
							@keypress.enter="
								AddComment();
								resetContent();
							"
							v-model="commentContent"
							type="text"
							class="comment"
							placeholder="댓글달기"
						/>
						<button
							@click.prevent="
								AddComment();
								resetContent();
							"
							class="comment-btn"
						>
							게시
						</button>
					</div>
					<div class="comment-body">
						<ul>
							<li
								class="comment-list"
								v-for="comment in article.comments"
								:key="comment.id"
							>
								<div class="comment-contentbox">
									<p>
										<router-link
											tabindex="-1"
											class="comment-userbox"
											:to="`/profile/${comment.user.name}`"
										>
											<span class="comment-user"
												><img
													v-if="comment.user.profile_image !== null"
													:src="`${BaseUrl}${comment.user.profile_image}`"
													alt="유저댓글이미지"
												/>
												<img
													v-else
													:src="`${BaseUrl}upload/noProfile.png`"
													alt="유저댓글이미지"
												/>
												{{ comment.user.name }}</span
											>
										</router-link>
										<span class="comment-content-item" tabindex="0">{{
											comment.content
										}}</span>
									</p>
								</div>
								<div class="comment-btnbox">
									<i
										v-if="getName === comment.user.name"
										@click="removeComment(comment.id)"
										class="icon ion-md-close unlike"
										aria-label="댓글 취소"
										tabindex="0"
									>
									</i>
									<i
										v-if="comment.like.liked"
										@click="commentUnLike(comment.id)"
										class="icon ion-md-heart like"
										aria-label="댓글 좋아요 취소"
										tabindex="0"
									></i>
									<i
										v-else
										@click="commentLike(comment.id)"
										class="icon ion-md-heart unlike"
										aria-label="댓글 좋아요"
										tabindex="0"
									></i>
								</div>
							</li>
						</ul>
					</div>
				</section>
			</article>
		</section>
	</section>
</template>

<script>
import bus from '@/utils/bus.js';
import { mapGetters } from 'vuex';
import {
	fetchArticle,
	createComment,
	deleteComment,
	createArticleLike,
	deleteArticleLike,
	createArticleCommentLike,
	deleteArticleCommentLike,
	createArticleBookmark,
	deleteArticleBookmark,
} from '@/api/articles';
import '@toast-ui/editor/dist/toastui-editor.css';
import Viewer from '@toast-ui/vue-editor/src/Viewer.vue';
export default {
	props: {
		id: Number,
		board_name: String,
		article_id: Number,
	},
	components: {
		Viewer,
	},
	data() {
		return {
			article: null,
			initText: null,
			commentContent: null,
		};
	},
	computed: {
		...mapGetters(['getName']),
		isLiked() {
			return this.article.like.liked;
		},
		likeCount() {
			return this.article.like.like_cnt;
		},
		isBookmarked() {
			return this.article.isfavorite;
		},
		BaseUrl() {
			return process.env.VUE_APP_API_URL;
		},
		FileCheck() {
			return this.FilenameCheck(this.article.file);
		},
		writerImg() {
			return this.article.user.profile_image !== null
				? `${this.BaseUrl}${this.article.user.profile_image}`
				: `${this.BaseUrl}upload/noProfile.png`;
		},
	},

	methods: {
		FilenameCheck(filename) {
			var _fileLen = filename.length;

			var _lastDot = filename.lastIndexOf('.');

			// 확장자 명만 추출한 후 소문자로 변경
			var _fileExt = filename.substring(_lastDot, _fileLen).toLowerCase();

			return this.checkExtension(_fileExt);
		},
		checkExtension(fileExt) {
			var _fileExt = fileExt;
			var _result;

			// 미리보기 가능한 확장자는 배열에 넣기
			var _arrExt = new Array('.pdf', '.bmp', '.gif', '.jpg', '.png', '.jpeg');

			// 배열 요소를 검사하여 체크
			for (var i = 0; i < _arrExt.length; i++) {
				if (_arrExt[i] == _fileExt) {
					_result = true;
					break;
				} else _result = false;
			}

			return _result;
		},
		async fetchData() {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				const articleId = this.article_id;
				const { data } = await fetchArticle(studyId, boardName, articleId);
				this.article = data;
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
				if (error.response.status === 404 || error.response.status === 500) {
					this.$router.push('/404');
				}
			}
		},
		isCommentLiked(flag) {
			return flag;
		},
		async addBookmark() {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				const articleId = this.article_id;
				await createArticleBookmark(studyId, boardName, articleId);
				this.fetchData();
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
				if (error.response.status === 404) {
					this.$router.push('/404');
				}
			}
		},
		async removeBookmark() {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				const articleId = this.article_id;
				await deleteArticleBookmark(studyId, boardName, articleId);
				this.fetchData();
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
			}
		},
		async AddComment() {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				const articleId = this.article_id;
				const content = this.commentContent;
				await createComment(studyId, boardName, articleId, {
					content,
				});
				this.fetchData();
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
			}
		},
		async removeComment(commentId) {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				const articleId = this.article_id;
				await deleteComment(studyId, boardName, articleId, commentId);
				this.fetchData();
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
			}
		},
		async articleLike() {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				const articleId = this.article_id;
				await createArticleLike(studyId, boardName, articleId);
				this.fetchData();
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
			}
		},
		async articleUnLike() {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				const articleId = this.article_id;
				await deleteArticleLike(studyId, boardName, articleId);
				this.fetchData();
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
			}
		},
		async commentLike(commentId) {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				const articleId = this.article_id;
				await createArticleCommentLike(
					studyId,
					boardName,
					articleId,
					commentId,
				);
				this.fetchData();
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
			}
		},
		async commentUnLike(commentId) {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				const articleId = this.article_id;
				await deleteArticleCommentLike(
					studyId,
					boardName,
					articleId,
					commentId,
				);
				this.fetchData();
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
			}
		},
		async removeArticle() {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				const articleId = this.article_id;
				bus.$emit('show:delete', { studyId, boardName, articleId });
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
			}
		},
		resetContent() {
			this.commentContent = null;
		},
		editRouter() {
			this.$router.push(
				`/study/${this.id}/${this.board_name}/${this.article_id}/edit`,
			);
		},
	},
	created() {
		this.fetchData();
	},
	watch: {
		$route: 'fetchData',
	},
};
</script>

<style lang="scss">
.comment-list {
	display: flex;
	justify-content: space-between;
}
.comment-btnbox {
	align-self: flex-start;
	font-size: $font-normal;
	i {
		margin-right: 5px;
	}
}
.content-info {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin: 10px 0 15px;
	padding-left: 10px;

	img {
		height: 20px;
		width: 20px;
		border-radius: 50%;
		margin-right: 5px;
	}
	a {
		display: flex;
		align-items: center;
		margin-right: 15px;
		font-size: $font-bold * 0.65;
		font-weight: 600;
		color: #4b4b4b;
	}
	span {
		font-size: $font-normal * 0.9;
	}
}
.breadcrumb {
	ol {
		padding: 0;
	}
	li {
		display: inline;
	}
	a {
		color: rgb(136, 136, 136);
		font-size: $font-light;
		text-decoration: none;
	}
	span {
		margin: 5px;
	}
}
.card-detail-wrap {
	width: 100%;
	margin-bottom: 50px;
	padding: 15px 0;
	border-radius: 3px;
}
.card-detail-title {
	display: flex;
	margin: 10px;
	height: 2.5rem;
	p {
		font-size: $font-bold;
	}
}
.card-detail-filebox {
	margin-top: 2rem;
	font-size: font-normal;
	.card-detail-file {
		font-weight: bold;
		margin-right: 5px;
	}
}
.img {
	width: 50px;
	height: 50px;
	border-radius: 50%;
}
.card-detail-content {
	padding: 10px;
	.tui-editor-contents {
		p {
			width: 100%;
			height: 100%;
			word-break: break-all;
			.colour {
				word-break: break-all;
			}
			img {
				width: 100%;
				height: 100%;
				object-fit: cover;
			}
		}
	}
	.main-img {
		width: 100%;
		margin: 30px 0;
	}
	.logo {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin: 10px 0 10px;
		color: crimson;
		font-size: $font-bold;
		.logo-likebox {
			display: flex;
			align-items: center;
		}
		.logo-btnbox {
			display: flex;
			align-items: center;
			.span-btn {
				margin-left: 5px;
			}
		}
		i {
			margin-right: 5px;
			cursor: pointer;
		}
		span {
			font-size: $font-light;
			color: #999999;
		}
		.bookmark {
			color: #ffe066;
		}
	}

	.comment-body {
		li {
			display: flex;
			align-items: center;
			margin: 15px 0;
		}
	}
	.comment-contentbox {
		width: 90%;
		// word-break: break-all;
		p {
			display: flex;
		}
		.comment-content-item {
			word-break: break-all;
		}
	}
	.comment-form {
		display: flex;
		align-items: center;
		position: relative;
		.comment {
			width: 90%;
			height: 45px;
			padding: 0.8rem;
			border: none;
			border-top: 1px solid #dbdbdb;
			color: #999999;
			font-size: $font-normal;
			&:focus {
				outline: none;
			}
		}
		.comment-btn {
			position: absolute;
			top: 15px;
			right: 10px;
			background: none;
			border: none;
			color: $main-color;
			cursor: pointer;
		}
	}
	.comment-userbox {
		display: flex;
		align-items: flex-start;
	}
	.comment-user {
		margin-right: 8px;
		font-weight: bold;
		cursor: pointer;
		display: flex;
		word-break: keep-all;
		align-items: center;
		img {
			height: 20px;
			width: 20px;
			border-radius: 50%;
			margin-right: 5px;
		}
	}
}
.like {
	color: crimson;
	cursor: pointer;
}
.unlike {
	color: grey;
	cursor: pointer;
}
.span-btn {
	cursor: pointer;
}
</style>
