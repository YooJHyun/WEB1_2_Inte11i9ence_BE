package com.prgrmsfinal.skypedia.post.scheduler;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.member.repository.MemberRepository;
import com.prgrmsfinal.skypedia.post.entity.Post;
import com.prgrmsfinal.skypedia.post.entity.PostLikes;
import com.prgrmsfinal.skypedia.post.repository.PostLikesRepository;
import com.prgrmsfinal.skypedia.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostScheduler {
	private final RedisTemplate<String, String> redisTemplate;

	private final PostRepository postRepository;

	private final MemberRepository memberRepository;

	private final PostLikesRepository postLikesRepository;

	@Value("${post.views.prefix.key}")
	private String POST_VIEWS_PREFIX_KEY;

	@Value("${post.likes.prefix.key}")
	private String POST_LIKES_PREFIX_KEY;

	@Value("${post.unlikes.prefix.key}")
	private String POST_UNLIKES_PREFIX_KEY;

	@Transactional
	@Scheduled(fixedRate = 10000)
	public void syncViewsCacheToDB() {
		Map<Object, Object> viewsMap = redisTemplate.opsForHash().entries(POST_VIEWS_PREFIX_KEY);

		viewsMap.entrySet().forEach(e -> {
			Long id = Long.parseLong(e.getKey().toString());
			Long views = Long.parseLong(e.getValue().toString());

			if (postRepository.incrementViewsById(id, views) == 0) {
				log.warn("DB Sync error: post not exists. (postId={},views={})", id, views);
			}
			;
		});

		redisTemplate.delete(POST_VIEWS_PREFIX_KEY);
	}

	@Transactional
	@Scheduled(fixedRate = 10000)
	public void syncLikesCacheToDB() {
		Set<String> keySet = redisTemplate.keys(StringUtils.join(POST_LIKES_PREFIX_KEY, "*"));

		if (keySet != null) {
			keySet.forEach(key -> {
				Long postId = Long.parseLong(key.replace(POST_LIKES_PREFIX_KEY, ""));
				AtomicLong likes = new AtomicLong(redisTemplate.opsForSet().size(key));
				Set<String> values = redisTemplate.opsForSet().members(key);

				values.forEach(value -> {
					Long memberId = Long.parseLong(value);

					try {
						if (postLikesRepository.existsByPostIdAndMemberId(postId, memberId)) {
							likes.decrementAndGet();

							throw new IllegalArgumentException(StringUtils.join(
								"data already exists. [postId=", postId, ", memberId=", memberId, "]"));
						}

						Member member = memberRepository.findById(memberId).orElseThrow(()
							-> new NoSuchElementException(
							StringUtils.join("invalid member id. [memberId=", memberId, "]")));

						Post post = postRepository.findById(postId).orElseThrow(()
							-> new NoSuchElementException(StringUtils.join("invalid post id. [postId=", postId, "]")));

						postLikesRepository.save(PostLikes.builder()
							.post(post)
							.member(member)
							.build());
					} catch (IllegalArgumentException | NoSuchElementException e) {
						log.warn("DB Sync error: {}", e.getMessage());
					}
				});

				redisTemplate.delete(StringUtils.join(POST_LIKES_PREFIX_KEY, postId));

				postRepository.incrementOrDecrementLikesById(postId, likes.get());
			});
		}
	}

	@Transactional
	@Scheduled(fixedRate = 10000)
	public void syncUnlikesCacheToDB() {
		Set<String> keySet = redisTemplate.keys(StringUtils.join(POST_UNLIKES_PREFIX_KEY, "*"));

		if (keySet != null) {
			keySet.forEach(key -> {
				Long postId = Long.parseLong(key.replace(POST_UNLIKES_PREFIX_KEY, ""));
				AtomicLong unlikes = new AtomicLong(redisTemplate.opsForSet().size(key));
				Set<String> values = redisTemplate.opsForSet().members(key);

				values.forEach(value -> {
					Long memberId = Long.parseLong(value);

					try {
						if (!postLikesRepository.existsByPostIdAndMemberId(postId, memberId)) {
							unlikes.updateAndGet(unlike -> (unlike == 0) ? unlike : unlike + 1);
							throw new IllegalArgumentException(StringUtils.join(
								"data not exists. [postId=", postId, ", memberId=", memberId, "]"
							));
						}

						postLikesRepository.delete(postLikesRepository.findByPostIdAndMemberId(postId, memberId)
							.orElseThrow(() -> new NoSuchElementException(StringUtils.join(
								"invalid memberId or postId. [postId=", postId, ", memberId=", memberId, "]")
							)));
					} catch (IllegalArgumentException | NoSuchElementException e) {
						log.warn("DB Sync error: {}", e.getMessage());
					}
				});

				redisTemplate.delete(StringUtils.join(POST_UNLIKES_PREFIX_KEY, postId));

				postRepository.incrementOrDecrementLikesById(postId, unlikes.get());
			});
		}
	}
}
