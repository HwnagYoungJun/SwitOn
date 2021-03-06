<template>
	<section
		class="auto-complete"
		@keyup.down="selectStudy('down')"
		@keyup.up="selectStudy('up')"
	>
		<section class="main-logo-wrap">
			<img
				v-if="isMainRoute"
				src="@/assets/white.png"
				alt="switon-logo"
				id="main-logo"
				:class="[isMainRoute ? 'switon-pos-block' : '']"
			/>
			<label for="search" class="a11y-hidden">search: </label>
			<input
				autocomplete="off"
				v-model="searchData"
				v-on:input="fetchAutoComplete"
				type="search"
				id="search"
				:class="[isMainRoute ? 'main-input' : 'search']"
				placeholder="소모임을 검색하세요"
				@keyup.enter="moveOnlyStudy"
			/>
			<button @click="moveOnlyStudy" class="a11y-hidden">
				검색
			</button>
		</section>
		<section
			v-if="
				searchedStudyData.length ||
					searchedUpperData.length ||
					searchedLowerData.length
			"
			:class="['searched-datas', isMainRoute ? 'main-searched-datas' : '']"
		>
			<p v-if="searchedStudyData">스터디</p>
			<ul v-if="searchedStudyData">
				<li
					v-for="data in searchedStudyData"
					:key="data.id"
					@click="moveStudy(data.id)"
					@keyup.enter="moveStudy(data.id)"
					tabindex="0"
				>
					{{ data.name }}
				</li>
			</ul>
			<p v-if="searchedUpperData">상위 카테고리</p>
			<ul v-if="searchedUpperData">
				<li
					v-for="data in searchedUpperData"
					:key="data.id"
					@click="moveUpperCategory(data.name)"
					@keyup.enter="moveUpperCategory(data.name)"
					tabindex="0"
				>
					{{ data.name }}
				</li>
			</ul>
			<p v-if="searchedLowerData">하위 카테고리</p>
			<ul v-if="searchedLowerData">
				<li
					v-for="data in searchedLowerData"
					:key="data.id"
					@click="moveLowerCategory(data)"
					@keyup.enter="moveLowerCategory(data)"
					tabindex="0"
				>
					{{ data.name }}
				</li>
			</ul>
		</section>
	</section>
</template>

<script>
import bus from '@/utils/bus.js';
import { searchStudy } from '@/api/studies.js';
// 방향키 index
let index = -1;

export default {
	data() {
		return {
			searchData: '',
			searchedUpperData: [],
			searchedLowerData: [],
			searchedStudyData: [],
		};
	},
	computed: {
		isMainRoute() {
			return this.$route.name === 'main';
		},
	},
	methods: {
		async fetchAutoComplete(e) {
			try {
				if (!e.target.value) {
					this.searchedUpperData = [];
					this.searchedLowerData = [];
					this.searchedStudyData = [];
					return;
				}
				const { data } = await searchStudy(e.target.value);

				this.searchedUpperData = data.uppercategories;
				this.searchedLowerData = data.lowercategories;
				this.searchedStudyData = data.studies;
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
			}
		},
		clearSearchedData() {
			this.searchData = '';
			this.searchedUpperData = [];
			this.searchedLowerData = [];
			this.searchedStudyData = [];
		},
		moveOnlyStudy(e) {
			const target = e.target.value ? e.target.value : this.searchData;
			if (!target) {
				bus.$emit('show:toast', '검색어를 입력해주세요');
				return;
			}
			this.searchData = '';
			if (document.querySelector('.searched-datas')) {
				document.querySelector('.searched-datas').style.display = 'none';
			}
			this.$router.push(`/study/search/${target}`);
		},
		moveStudy(id) {
			this.clearSearchedData();
			this.$router.push(`/study/${id}`);
			index = -1;
		},
		moveUpperCategory(name) {
			this.clearSearchedData();
			this.$router.push(`/category/${name}`);
		},
		moveLowerCategory(data) {
			this.clearSearchedData();
			this.$router.push(`/category/${data.upper_name}/${data.name}`);
		},
		// 방향키
		selectStudy(key) {
			const search = document.querySelector('#search');
			let list = document.querySelectorAll('.searched-datas li');
			let size = list.length - 1;
			if (key === 'down' && index < size) {
				index++;
			} else if (key === 'up' && index > 0) {
				index--;
			} else {
				search.focus();
				index = -1;
				return;
			}
			list[index].focus();
		},
		searchBlur() {},
	},
	mounted() {
		window.addEventListener('click', function(e) {
			if (document.querySelector('.searched-datas')) {
				const searchDatas = document.querySelector('.searched-datas');
				if (
					e.target.nodeName !== 'INPUT' &&
					e.target.nodeName !== 'LI' &&
					searchDatas
				) {
					searchDatas.style.display = 'none';
				}
			}
		});
	},
};
</script>

<style lang="scss">
.search {
	width: 400px;
	padding: 0.8rem 2rem 0.8rem 0.75rem;
	font-size: $font-light;
	border: 1px solid #dbdbdb;
	color: -internal-light-dark(black, white);
	box-shadow: rgba(0, 0, 0, 0.04) 0px 0px 1px 0px,
		rgba(41, 42, 43, 0.16) 0px 1px 3px 0px;
	border-radius: 3px;
	transition: all 0.35s ease;
	&:focus {
		outline: none;
		background: rgb(248, 248, 249);
		border: none;
		width: 420px;
		box-shadow: none;
	}
	@media screen and (max-width: 992px) {
		width: 200px;
		&:focus {
			width: 220px;
		}
	}
}
.main-input {
	width: 400px;
	padding: 13px 25px;
	line-height: 2;
	border: none;
	border-radius: 4px;
	background: rgba(255, 255, 255, 0.5);
	box-shadow: 3px 3px 5px rgba(83, 83, 83, 0.35);
	&:focus {
		outline: none;
		background: rgba(255, 255, 255, 1);
	}
	@media screen and (max-width: 768px) {
		width: 250px;
		margin-bottom: 50px;
		padding-left: 60px;
		&:focus {
			width: 270px;
		}
	}
}
.auto-complete {
	position: relative;
	.main-logo-wrap {
		position: relative;
		.switon-pos-block {
			display: block;
			position: absolute;
			top: 8.5px;
			left: 7px;
			width: 35px;
			@media screen and (min-width: 769px) {
				display: none;
			}
		}
	}
	.searched-datas {
		width: 420px;
		position: absolute;
		top: 60px;
		background: #fff;
		box-shadow: 0 3px 7px rgb(214, 214, 214);
		z-index: 999;
		p {
			padding: 3px 10px;
			font-size: $font-light;
			font-weight: bold;
		}
		ul {
			padding: 0 10px 20px;
		}
		li {
			padding: 10px;
			&:hover {
				background: #f2f2f2;
				cursor: pointer;
			}
			&:focus {
				background: #f2f2f2;
				outline: none;
			}
		}
		hr {
			width: 95%;
			border: none;
			border-bottom: 1px solid #d1d1d1;
		}
		@media screen and (max-width: 768px) {
			width: 270px;
		}
	}
	.main-searched-datas {
		width: 400px;
		box-shadow: none;
		@media screen and (max-width: 768px) {
			width: 270px;
		}
	}
}
.a11y-hidden {
	overflow: hidden;
	position: absolute !important;
	top: -9999em;
	clip: rect(0, 0, 0, 0);
	width: 1px;
	height: 1px;
	margin: -1px;
}
</style>
