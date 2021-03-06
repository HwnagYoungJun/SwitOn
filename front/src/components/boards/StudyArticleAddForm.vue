<template>
	<form
		class="articleform"
		@submit.prevent="createArticle"
		enctype="multipart/form-data"
	>
		<header class="articleform-header">
			<h2>{{ routeBoardName }}</h2>
			<div class="articleform-btnbox">
				<button @click.prevent="$router.go(-1)" class="articleform-btn-cancle">
					취소
				</button>
				<button class="hide-btn">작성</button>
				<button class="articleform-btn-submit" type="submit">작성</button>
			</div>
		</header>
		<section class="articleform-main">
			<input
				type="text"
				placeholder="제목을 입력해주세요"
				class="articleform-input"
				v-model="title"
			/>
			<Editor
				ref="toastuiEditor"
				initialEditType="wysiwyg"
				:initialValue="editorText"
				:options="editorOptions"
				height="350px"
			/>
			<div class="upload-btn_wrap">
				<input
					v-model="fileRoute"
					type="text"
					class="upload_text"
					readonly="readonly"
					placeholder="첨부된 파일이 없습니다."
				/>
				<button type="button" title="첨부">
					<span>첨부</span>
				</button>
				<input
					ref="inputFile"
					type="file"
					class="input_file"
					title="첨부"
					@change="onChangeFile"
				/>
			</div>
		</section>
	</form>
</template>
<script>
import bus from '@/utils/bus.js';
import 'codemirror/lib/codemirror.css';
import '@toast-ui/editor/dist/toastui-editor.css';
import '@toast-ui/editor/dist/i18n/ko-kr.js';
import { Editor } from '@toast-ui/vue-editor';
import { createArticle } from '@/api/articles';

export default {
	props: {
		id: Number,
		board_name: String,
	},
	data() {
		return {
			title: '',
			editorText: '',
			fileRoute: '',
			inputFile: '',
			editorOptions: {
				language: 'ko-KR',
				hideModeSwitch: true,
				minHeight: '350px',
				placeholder: '내용을 입력해주세요',
			},
		};
	},
	computed: {
		routeBoardName() {
			return this.board_name.charAt(0).toUpperCase() + this.board_name.slice(1);
		},
	},
	components: {
		Editor,
	},
	methods: {
		async createArticle() {
			try {
				const studyId = this.id;
				const boardName = this.board_name;
				let content = this.$refs.toastuiEditor.invoke('getHtml');
				if (!this.title) {
					bus.$emit('show:toast', '제목을 입력해주세요');
					return;
				}
				if (!content.length) {
					bus.$emit('show:toast', '내용을 입력해주세요');
					return;
				}
				const { data } = await createArticle(studyId, boardName, {
					title: this.title,
					content,
					file: this.inputFile,
				});
				const articleId = data;
				this.$router.push(`/study/${studyId}/${boardName}/${articleId}`);
			} catch (error) {
				if (error.response.status === 500) {
					if (error.response.data.msg === undefined) {
						bus.$emit(
							'show:toast',
							'파일의 용량 제한을 초과했습니다. 최대 5mb까지 가능합니다.',
						);
					} else {
						bus.$emit('show:toast', `${error.response.data.msg}`);
					}
				} else {
					bus.$emit('show:toast', `${error.response.data.msg}`);
				}
			}
		},
		onChangeTitle(val) {
			this.title = val;
		},
		onChangeFile(e) {
			this.fileRoute = e.target.value;
			this.inputFile = this.$refs.inputFile.files[0];
		},
	},
};
</script>
<style lang="scss">
.articleform {
	width: 100%;
	height: 100%;
}
.articleform-main {
	box-shadow: 0 2px 6px 0 rgba(68, 67, 68, 0.4);
	padding: 1rem;
	border-radius: 4px;
	.articleform-input {
		width: 100%;
		padding: 10px;
		border: none;
		border-radius: 0;
		border-bottom: 1px solid black;
		&:focus {
			outline: none;
			border-bottom: 1px solid black;
		}
	}
	.tui-editor-defaultUI {
		border: none;
	}
	.tui-editor-defaultUI-toolbar {
		padding: 0 10px;
	}
	.te-ww-container .tui-editor-contents:first-child {
		padding: 1rem 10px 0 10px;
	}
}
.articleform-btnbox {
	display: flex;
	align-items: center;
	position: relative;
}

.articleform-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 1rem;
	.articleform-btn-cancle {
		@include form-btn('white');
		margin-right: 5px;
	}
	.articleform-btn-submit {
		@include form-btn('purple');
		position: relative;
		right: 0;
	}
	.hide-btn {
		border: none;
		border-radius: 3px;
		height: 40px;
		padding: 0 1.125rem;
		font-size: 1rem;
		font-weight: 700;
		text-decoration: none solid #000;
		background: #000;
		color: #fff;
		position: absolute;
		right: 0;
	}
}
input.upload_text {
	flex: 1;
	height: 2rem;
	margin-top: 1rem;
}
div.upload-btn_wrap input.input_file {
	/*파일찾기 폼 투명하게*/
	position: absolute;
	top: 0;
	right: 0;
	@include scale(width, 75px);
	opacity: 0;
	filter: alpha(opacity=0);
	-ms-filter: 'alpha(opacity=0)';
	-moz-opacity: 0;
	margin-top: 0.6rem;
	&:hover {
		cursor: pointer;
	}
}
div.upload-btn_wrap {
	position: relative;
	display: flex;
	align-items: center;
	width: 100%; /*width, height 값은 button(찾아보기)값과 같아야함 */
	height: 2rem;
	border-top: 1px solid #bbb;
	padding-left: 3px;
	margin-top: 1rem;
}
div.upload-btn_wrap button {
	width: 70px;
	@media screen and (max-width: 768px) {
		width: 50px;
	}
	height: 2rem;
	font-weight: bold;
	background: rgb(225, 225, 225);
	border: none;
	border-radius: 3px;
	color: rgb(150, 149, 149);
	margin-top: 1rem;
}
</style>
