package com.server.domain.member.entity;

import javax.persistence.*;

import com.server.domain.answer.entity.Answer;
import com.server.domain.cart.entity.Cart;
import com.server.domain.channel.entity.Channel;
import com.server.domain.order.entity.Order;
import com.server.domain.reply.entity.Reply;
import com.server.domain.subscribe.entity.Subscribe;
import com.server.domain.watch.entity.Watch;
import com.server.global.entity.BaseEntity;

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

	private int reward;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Order> orders;

	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
	private Channel channel;

	@OneToMany(mappedBy = "member")
	@Builder.Default
	private List<Answer> answers = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	@Builder.Default
	private List<Cart> carts = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	@Builder.Default
	private List<Watch> watches = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	@Builder.Default
	private List<Reply> replies = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	@Builder.Default
	private List<Subscribe> subscribes = new ArrayList<>();

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
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
		this.reward -= reward;
	}
}
