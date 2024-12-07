package com.prgrmsfinal.skypedia.reply.service;

import org.springframework.security.core.Authentication;

import com.prgrmsfinal.skypedia.member.entity.Member;
import com.prgrmsfinal.skypedia.post.dto.PostRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyRequestDTO;
import com.prgrmsfinal.skypedia.reply.dto.ReplyResponseDTO;
import com.prgrmsfinal.skypedia.reply.entity.Reply;

public interface ReplyService {
	ReplyResponseDTO.ReadAll readAll(Authentication authentication, Long parentId, Long lastReplyId);

	Reply create(PostRequestDTO.CreateReply request, Member member);

	void modify(Authentication authentication, Long replyId, ReplyRequestDTO.Modify request);

	void delete(Authentication authentication, Long replyId);

	void restore(Authentication authentication, Long replyId);

	ReplyResponseDTO.ToggleLikes toggleLikes(Authentication authentication, Long replyId);

	Long getLikes(Reply reply);

	boolean isCurrentMemberLiked(Authentication authentication, Reply reply);
}