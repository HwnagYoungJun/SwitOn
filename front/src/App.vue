<template>
	<section id="app">
		<Main v-if="isMainRoute" />
		<section v-else class="main-wrap">
			<AppHeader v-if="!isAccountsRoute"></AppHeader>
			<main :class="[!isAccountsRoute ? 'main-container' : '']">
				<router-view />
			</main>
			<footer v-if="!isAccountsRoute"><Footer /></footer>
		</section>
		<ToastPopup></ToastPopup>
		<ToastPopupDelete></ToastPopupDelete>
		<ToastPopupTerm></ToastPopupTerm>
	</section>
</template>

<script>
import bus from '@/utils/bus';
import AppHeader from '@/components/common/AppHeader.vue';
import Footer from '@/components/common/Footer.vue';
import Main from '@/views/Main.vue';
import ToastPopup from './components/common/ToastPopup.vue';
import ToastPopupDelete from './components/common/ToastPopupDelete.vue';
import ToastPopupTerm from './components/common/ToastPopupTerm.vue';
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';
import { mapGetters } from 'vuex';
export default {
	components: {
		AppHeader,
		Footer,
		ToastPopup,
		Main,
		ToastPopupDelete,
		ToastPopupTerm,
	},
	computed: {
		...mapGetters(['getUserId', 'isLogin']),
		isAccountsRoute() {
			return this.$route.name === 'signUp' || this.$route.name === 'login';
		},
		isMainRoute() {
			return this.$route.name === 'main';
		},
	},
	data() {
		return {
			messages: [],
		};
	},
	methods: {
		connect() {
			const Id = this.getUserId;
			let ServerUrl = process.env.VUE_APP_API_URL;
			let client = Stomp.over(new SockJS(`${ServerUrl}websocket`));
			client.connect({}, function() {
				client.subscribe(`/topic/notification/${Id}`, message => {
					bus.$emit('show:toast', JSON.parse(message.body).msg);
				});
			});
		},
	},
	mounted() {
		if (this.isLogin) {
			// this.connect();
		}
	},
	watch: {
		$route() {
			if (this.isLogin) {
				// this.connect();
			}
		},
	},
};
</script>

<style lang="scss">
@import './assets/css/reset.css';
@import './assets/css/common.css';

.main-wrap {
	display: flex;
	min-height: 100vh;
	flex-direction: column;
}
.main-container {
	flex: 1;
	width: 70%;
	margin: 0 auto;
}
@media screen and (max-width: 768px) {
	.main-container {
		width: 95%;
	}
	.nav-router {
		width: 100%;
		height: 37px;
		justify-content: space-around;
		position: fixed;
		bottom: 0;
		right: 0;
		z-index: 888;
	}
}
</style>
