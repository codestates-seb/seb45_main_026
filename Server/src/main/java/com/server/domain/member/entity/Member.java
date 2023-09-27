package com.server.domain.member.entity;

import javax.persistence.*;

import com.server.domain.account.domain.Account;
import com.server.domain.answer.entity.Answer;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.order.entity.Order;
import com.server.domain.reply.entity.Reply;
import com.server.domain.reward.entity.Reward;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.watch.entity.Watch;
import com.server.global.entity.BaseEntity;

import com.server.global.exception.businessexception.memberexception.MemberAccessDeniedException;
import com.server.global.exception.businessexception.memberexception.MemberNotUpdatedException;
import com.server.global.exception.businessexception.orderexception.RewardNotEnoughException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Authority authority;

	private String imageFile;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private Grade grade = Grade.BRONZE;

	private int gradePoint;

	private int reward;
	
	@OneToMany(mappedBy = "member")
	@Builder.Default
	private List<Order> orders = new ArrayList<>();

	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
	private Channel channel;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Account account;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Answer> answers = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Cart> carts = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Watch> watches = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Reply> replies = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Subscribe> subscribes = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Reward> rewards = new ArrayList<>();

	public boolean isAdmin() {

		return this.authority.equals(Authority.ROLE_ADMIN);
	}

	public void updatePassword(String password) {
		if (password == null || password.equals(this.password)) {
			throw new MemberNotUpdatedException();
		}

		this.password = password;
	}

	public void updateNickname(String nickname) {
		if (nickname == null || nickname.equals(this.nickname)) {
			throw new MemberNotUpdatedException();
		}

		this.nickname = nickname;
	}


	public void updateImageFile(String imageFile) {
		this.imageFile = this.memberId + "/profile/" + imageFile;
	}

	public void deleteImageFile() {
		this.imageFile = null;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
		if (this.channel.getMember() != this) {
			this.channel.setMember(this);
		}
	}

	public static Member createMember(String email, String password, String nickname) {
		return Member.builder()
			.email(email)
			.password(password)
			.nickname(nickname)
			.authority(Authority.ROLE_USER)
			.build();
	}

	public void addReward(int reward) {
		this.reward += reward;
	}

	public void minusReward(int reward) {
		checkEnoughReward(reward);
		this.reward -= reward;
	}

	public void addGradePoint(int gradePoint) {
		this.gradePoint += gradePoint;
		this.grade = Grade.getGrade(this.gradePoint);
	}

	public void minusGradePoint(int gradePoint) {
		this.gradePoint -= gradePoint;
		this.grade = Grade.getGrade(this.gradePoint);
	}
  
	public void checkReward(int reward) {
		checkEnoughReward(reward);
	}

	public boolean hasEnoughReward(int reward) {
		return this.reward - reward >= 0;
	}

	private void checkEnoughReward(int reward) {
		if(this.reward - reward < 0) throw new RewardNotEnoughException();
	}

	public void updateAccount(Account account) {
		this.account = account;
	}
}
