<template>
	<header id="nav" :class="['container', isMainRoute ? 'nav-white' : '']">
		<div class="nav-search">
			<div class="nav-logo">
				<router-link class="switon-logo" :to="`/`">
					<img
						v-if="isMainRoute"
						src="@/assets/white.png"
						alt="스윗온 로고 이미지"
						:class="['switon', isMainRoute ? 'switon-pos' : '']"/>
					<img
						v-else
						src="@/assets/color.png"
						alt="스윗온 로고 이미지"
						class="switon"
				/></router-link>
			</div>
			<Search />
		</div>
		<nav
			:class="[
				'nav-router',
				isMainRoute ? 'nav-router-color' : 'nav-router-white',
			]"
		>
			<template v-if="!isUserLogin">
				<router-link class="nav-router-item" :to="{ name: 'login' }"
					><span class="nav-router-full">로그인</span>
					<i
						class="icon ion-md-log-in nav-router-medium"
						aria-labelledby="로그인 버튼"
					></i>
				</router-link>
			</template>
			<template v-else>
				<router-link class="nav-router-item" :to="'/category/인기'"
					><span class="nav-router-full">카테고리</span
					><i
						class="icon ion-md-apps nav-router-medium"
						aria-labelledby="카테고리 버튼"
					></i
				></router-link>
				<router-link class="nav-router-item" :to="{ name: 'newsfeed' }"
					><span class="nav-router-full">뉴스피드</span
					><i
						class="icon ion-md-list-box nav-router-medium"
						aria-labelledby="뉴스피드 버튼"
					></i
				></router-link>
				<router-link
					class="nav-router-item app-header-mobile-none"
					:to="{ name: 'addstudy' }"
					><span class="nav-router-full">만들기</span></router-link
				>
				<img
					v-if="isMainRoute"
					src="@/assets/white.png"
					alt="스윗온 로고 이미지"
					class="go-to-top-logo"
					@click="goToTop"
				/>
				<img
					v-else
					src="@/assets/black.png"
					alt="스윗온 로고 이미지"
					class="go-to-top-logo"
					@click="goToTop"
				/>
				<div class="nav-router-item" tabindex="0">
					<Notification :messages="messages" />
				</div>
				<a
					class="nav-router-item app-header-mobile-none"
					href="javascript:;"
					@click="logoutUser"
					><span class="nav-router-full">로그아웃</span></a
				>
				<router-link
					v-if="name"
					class="nav-router-item"
					:to="`/profile/${name}/`"
					><img
						v-if="profileImg"
						class="nav-router-img"
						:src="`${baseURL}${profileImg}`"
						:alt="`${name}의 프로필사진`"
					/>
					<i v-else class="icon ion-md-contact" aria-label="프로필 버튼"></i>
				</router-link>
			</template>
		</nav>
	</header>
</template>

<script>
import cookies from 'vue-cookies';
import bus from '@/utils/bus';
import { fetchUserAlarms } from '@/api/auth';
import { mapMutations, mapState, mapGetters } from 'vuex';
import { baseAuth } from '@/api/index';
import Search from '@/components/common/Search.vue';
import Notification from '@/components/common/Notification.vue';

export default {
	data() {
		return {
			userName: this.name ? this.name : null,
			profileImg: null,
			messages: [],
		};
	},
	components: {
		Search,
		Notification,
	},
	computed: {
		...mapGetters(['isLogin']),
		...mapState(['name']),
		baseURL() {
			return process.env.VUE_APP_API_URL;
		},
		isUserLogin() {
			return this.$store.getters.isLogin;
		},
		isMainRoute() {
			return this.$route.name === 'main';
		},
	},
	methods: {
		...mapMutations(['clearUserEmail', 'clearToken']),
		async fetchImg() {
			try {
				const { data } = await baseAuth.get('accounts/');
				this.profileImg = data.profile_image;
			} catch (error) {
				bus.$emit('show:toast', '로그인이 필요합니다');
				this.logoutUser();
			}
		},
		logoutUser() {
			this.clearUserEmail();
			this.clearToken();
			this.$cookies.remove('auth-token');
			this.$cookies.remove('name');
			this.$cookies.remove('userid');
			this.$router.push('/');
		},
		onChangeSearch(val) {
			this.searchData = val;
		},
		goToTop() {
			document.documentElement.scrollTop = 0;
		},
		async fetchData() {
			try {
				const { data } = await fetchUserAlarms();
				this.messages = data;
			} catch (error) {
				this.logoutUser();
			}
		},
	},
	watch: {
		$route() {
			if (cookies.isKey('auth-token')) {
				this.fetchImg();
				this.fetchData();
			} else {
				this.logoutUser();
			}
		},
	},
	created() {
		if (cookies.isKey('auth-token')) {
			this.fetchImg();
			this.fetchData();
		} else {
			this.logoutUser();
		}
	},
};
</script>

<style lang="scss">
header {
	height: 70px;
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 0.75rem;
	background-color: #fff;
	@media screen and (max-width: 640px) {
		justify-content: flex-start;
	}
}
.list-block {
	display: block;
}
.list-none {
	display: none;
}
.nav-white {
	background: none;
	color: white;
	#search {
		display: none;
	}
	#main-logo {
		display: none;
	}
	.nav-router-item {
		color: white;
	}
	i {
		color: white;
		font-size: 24px;
		font-weight: 600;
	}
}
.switon-logo {
	display: flex;
	align-items: center;
}
.switon {
	@include scale(width, 50px);
}
.nav-search {
	position: relative;
	display: flex;
	align-items: center;
	.nav-logo {
		@include scale(margin-right, 32px);
	}
	.nav-search-icon {
		position: absolute;
		right: 0;
		font-size: $font-bold;
		color: -internal-light-dark(black, white);
		margin: 0.5rem;
	}
}
.nav-router {
	display: flex;
	height: 100%;
	align-items: center;
	font-weight: 600;
	.nav-router-item {
		margin-right: 0.9rem;
	}
	.nav-router-img-item {
		width: 24px;
		height: 24px;
	}
	.nav-router-full {
		display: inline;
	}
	.nav-router-medium {
		display: none;
	}
	.nav-router-img {
		width: 24px;
		height: 24px;
		border-radius: 50%;
		object-fit: cover;
	}
	.go-to-top-logo {
		width: 24px;
		height: 24px;
		border-radius: 5px;
		object-fit: cover;
		display: none;
	}
	.app-header-mobile {
		display: none;
	}
	@media screen and (max-width: 768px) {
		.nav-router {
			position: fixed;
		}
		.main-page {
			position: relative;
		}
		.nav-router-full {
			display: none;
		}
		.nav-router-medium {
			display: inline;
		}
		i {
			font-size: 24px;
		}
		.app-header-mobile-none {
			display: none;
		}
		.app-header-mobile {
			display: inline;
		}
		.go-to-top-logo {
			display: inline;
			&:hover {
				cursor: pointer;
			}
		}
	}
}
.nav-router-white {
	background: #fff;
	i {
		color: rgb(97, 97, 97);
	}
	.nav-router-full {
		color: #495057;
	}
}
.nav-router-color {
	@media screen and (max-width: 1024px) {
		background: none;
		@media screen and (max-width: 768px) {
			background: $btn-purple;
		}
	}
}
.switon-pos {
	@media screen and (max-width: 768px) {
		display: none;
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
