<template>
	<div class="dashboard-wrap">
		<div class="card-wrap">
			<router-link :to="{ name: 'repositoryDetail' }"
				><ArticleCard
			/></router-link>
			<ArticleCard />
			<ArticleCard />
		</div>
		<aside>
			<div class="schedule">
				<span class="schedule-title">소모임 일정</span>
				<ul>
					<li>07.20 Mon 11:00-13:00 <button class="active">참여중</button></li>
					<li>07.20 Mon 11:00-13:00 <button>참여하기</button></li>
					<li>07.20 Mon 11:00-13:00 <button>참여하기</button></li>
				</ul>
			</div>
			<div class="schedule">
				<span class="schedule-title">나의 일정</span>
				<ul>
					<li>07.20 Mon 11:00-13:00 <button class="active">출석</button></li>
					<li>07.20 Mon 11:00-13:00 <button>대기중</button></li>
					<li>07.20 Mon 11:00-13:00 <button>대기중</button></li>
				</ul>
			</div>
			<div class="schedule">
				<span class="schedule-title">나의 출석</span>
				<div class="attend-wrap">
					<div class="attend">
						<p>참여율</p>
						<div class="progress-bar">
							<span class="join-percent">35%</span>
						</div>
					</div>
					<div class="attend">
						<p>출석율</p>
						<div class="progress-bar">
							<span class="attend-percent">85%</span>
						</div>
					</div>
				</div>
			</div>
		</aside>
	</div>
</template>

<script>
import ArticleCard from '@/components/common/ArticleCard.vue';
export default {
	components: {
		ArticleCard,
	},
};
</script>

<style lang="scss" scoped>
.dashboard-wrap {
	display: flex;
	@media screen and (max-width: 1350px) {
		flex-direction: column-reverse;
	}
}
.card-wrap {
	display: flex;
	flex-direction: column;
	align-items: center;
	flex: 2;
	margin-top: 20px;
	@media screen and (max-width: 768px) {
		// row + wrap => 자동으로 길이 조절해줌
		flex-direction: row;
		flex-wrap: wrap;
		justify-content: center;
	}
}
aside {
	flex: 1.5;
}
.schedule {
	margin: 30px;
	padding: 25px 15px 15px;
	border: 1px solid rgb(228, 228, 228);
	border-radius: 4px;
	position: relative;
	color: rgb(138, 138, 138);
	.schedule-title {
		padding: 0 15px;
		position: absolute;
		top: -10px;
		color: rgb(90, 90, 90);
		background: #fff;
	}
	li {
		margin: 20px;
		@media screen and (max-width: 370px) {
			margin: 10px 20px;
		}
		button {
			display: inline-block;
			width: 100px;
			padding: 7px 0;
			border: 1px solid #6c23c0;
			border-radius: 30px;
			color: #6c23c0;
			background: none;
			position: absolute;
			right: 20px;
			&:focus {
				outline: none;
			}
			@media screen and (max-width: 370px) {
				width: 100%;
				margin: 8px 0;
				position: static;
				right: 0;
			}
		}
		.active {
			color: #fff;
			background: $btn-purple;
		}
	}
	.attend-wrap {
		.attend {
			p {
				margin-bottom: 10px;
			}
			.progress-bar {
				width: 100%;
				color: white;
				border-radius: 8px;
				background: rgb(233, 233, 233);
				.join-percent {
					display: inline-block;
					width: 35%;
					padding-right: 8px;
					text-align: right;
					border-radius: 5px 0 0 5px;
					background: $btn-purple;
				}
				.attend-percent {
					display: inline-block;
					width: 85%;
					padding-right: 8px;
					text-align: right;
					border-radius: 5px 0 0 5px;
					background: $btn-purple;
				}
			}
		}
	}
}
</style>