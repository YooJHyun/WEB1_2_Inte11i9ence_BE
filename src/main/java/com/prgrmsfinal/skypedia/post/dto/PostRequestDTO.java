package com.prgrmsfinal.skypedia.post.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.prgrmsfinal.skypedia.photo.dto.PhotoRequestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class PostRequestDTO {
	@Schema(title = "게시글 등록 요청 DTO", description = "게시글의 등록 요청에 사용하는 DTO입니다.")
	@Getter
	@AllArgsConstructor
	public static class Create {
		@Schema(title = "게시글 제목", description = "게시글 제목입니다.", example = "게시글 제목")
		@Length(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
		@NotBlank(message = "제목은 비워둘 수 없습니다.")
		@Pattern(regexp = "^(?!\\s).*(?<!\\s)$", message = "제목의 맨 앞과 뒤에는 공백이 들어갈 수 없습니다.")
		private final String title;

		@Schema(title = "게시글 내용", description = "게시글 내용입니다.", example = "게시글 내용")
		@Length(max = 2000, message = "내용은 2000자를 초과할 수 없습니다.")
		private final String content;

		@Schema(title = "게시글 카테고리", description = "게시글이 속할 카테고리명입니다.", example = "자유")
		@NotBlank(message = "카테고리는 비워둘 수 없습니다.")
		private final String category;

		@Schema(title = "게시글 해쉬태그", description = "게시글의 특징을 표현할 해쉬태그입니다.")
		private final List<String> tags;

		@Schema(title = "별점", description = "0.0 ~ 5.0점 사이의 별점입니다.", example = "5.0")
		@Max(value = 5, message = "별점은 5.0 초과일 수 없습니다.")
		@Min(value = 0, message = "별점은 0.0 미만일 수 없습니다.")
		private final Float rating;

		@Schema(title = "업로드할 이미지 정보", description = "업로드할 이미지 메타데이터입니다.")
		private final List<PhotoRequestDTO.Upload> photos;
	}

	@Schema(title = "게시글 수정 요청 DTO", description = "게시글의 수정 요청에 사용하는 DTO입니다.")
	@Data
	@AllArgsConstructor
	public static class Modify {
		@Schema(title = "게시글 제목", description = "게시글 제목입니다.", example = "게시글 제목")
		@Length(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
		@NotBlank(message = "제목은 비워둘 수 없습니다.")
		@Pattern(regexp = "^(?!\\s).*(?<!\\s)$", message = "제목의 맨 앞과 뒤에는 공백이 들어갈 수 없습니다.")
		private final String title;

		@Schema(title = "게시글 내용", description = "게시글 내용입니다.", example = "게시글 내용")
		@Length(max = 2000, message = "내용은 2000자를 초과할 수 없습니다.")
		private final String content;

		@Schema(title = "별점", description = "0.0 ~ 5.0점 사이의 별점입니다.", example = "5.0")
		@Max(value = 5, message = "별점은 5.0 초과일 수 없습니다.")
		@Min(value = 0, message = "별점은 0.0 미만일 수 없습니다.")
		private final Float rating;

		@Schema(title = "업로드할 이미지 정보", description = "업로드할 이미지 메타데이터입니다.")
		private final List<PhotoRequestDTO.Upload> uploads;

		@Schema(title = "삭제할 이미지 ID", description = "삭제할 이미지 ID 목록입니다.")
		private final List<Long> deletes;
	}
}
