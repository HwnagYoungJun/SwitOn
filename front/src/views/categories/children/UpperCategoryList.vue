<template>
	<section v-if="loading">
		<Loading />
	</section>
	<section class="category-container" v-else>
		<div v-if="!studies.length">
			<StudyNotFound />
		</div>
		<article v-else class="popular-wrap">
			<router-link
				:key="study.id"
				v-for="study in studies"
				:to="`/study/${study.id}`"
				tabindex="-1"
			>
				<MainCard :study="study" colorPick="black" />
			</router-link>
		</article>
	</section>
</template>

<script>
import bus from '@/utils/bus.js';
import axios from 'axios';
import MainCard from '@/components/common/MainCard.vue';
import { upperCategoryId } from '@/utils/category';
import Loading from '@/components/common/Loading.vue';
import StudyNotFound from '@/components/common/StudyNotFound.vue';
import { popularStudy } from '@/api/studies';

export default {
	components: {
		MainCard,
		Loading,
		StudyNotFound,
	},
	data() {
		return {
			studies: [],
			loading: false,
		};
	},
	props: {
		upperCategoryName: String,
	},
	computed: {
		baseURL() {
			return process.env.VUE_APP_API_URL;
		},
		categoryId() {
			return upperCategoryId(this.upperCategoryName);
		},
	},
	methods: {
		async fetchUpperStudy() {
			try {
				this.loading = true;
				const { data } = await axios.get(`${this.baseURL}study`, {
					params: {
						uppercategory_id: this.categoryId,
					},
				});
				this.studies = data;
				this.loading = false;
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
				this.$router.push('/404');
			}
		},
		async fetchData() {
			try {
				this.loading = true;
				const { data } = await popularStudy();
				this.studies = data;
				this.loading = false;
			} catch (error) {
				bus.$emit('show:toast', `${error.response.data.msg}`);
			}
		},
	},
	watch: {
		$route() {
			if (this.upperCategoryName === '인기') {
				this.fetchData();
			} else {
				this.fetchUpperStudy();
			}
		},
	},
	created() {
		if (this.upperCategoryName === '인기') {
			this.fetchData();
		} else {
			this.fetchUpperStudy();
		}
	},
};
</script>

<style lang="scss" scoped>
.category-container {
	height: 100%;
	margin-bottom: 3rem;
}
.noStudy {
	img {
		width: 100%;
	}
}
.popular-wrap {
	width: 100%;
	height: 100%;
	display: grid;
	grid-template-columns: repeat(4, 1fr);
	grid-auto-rows: 20rem;
	grid-gap: 1rem;
	@media screen and (max-width: 1024px) {
		grid-template-columns: repeat(3, 1fr);
		grid-auto-rows: 19rem;
	}
	@media screen and (max-width: 768px) {
		grid-template-columns: repeat(2, 1fr);
		grid-auto-rows: 21rem;
	}
	@media screen and (max-width: 400px) {
		grid-template-columns: repeat(2, 1fr);
		grid-auto-rows: 18.5rem;
	}
}
.study-box {
	display: flex;
	justify-content: flex-start;
	width: 100%;
	flex-wrap: wrap;
	.study-li {
		width: 20%;
		margin: 0 2.5%;
		overflow: hidden;
	}
	@media screen and (max-width: 1024px) {
		.study-li {
			width: 30%;
			margin: 0 1.65%;
			overflow: hidden;
		}
	}
	@media screen and (max-width: 640px) {
		.study-li {
			width: 45%;
			justify-content: space-between;
			overflow: hidden;
		}
	}
}
</style>
