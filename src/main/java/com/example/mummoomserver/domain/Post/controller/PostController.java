package com.example.mummoomserver.domain.Post.controller;

import com.example.mummoomserver.config.resTemplate.ResponeException;
import com.example.mummoomserver.config.resTemplate.ResponseTemplate;
import com.example.mummoomserver.config.resTemplate.ResponseTemplateStatus;
import com.example.mummoomserver.domain.Post.Post;
import com.example.mummoomserver.domain.Post.dto.PostIdxResponseDto;
import com.example.mummoomserver.domain.Post.dto.PostResponseDto;
import com.example.mummoomserver.domain.Post.dto.PostSaveRequestDto;
import com.example.mummoomserver.domain.Post.dto.PostUpdateRequestDto;
import com.example.mummoomserver.domain.Post.service.PostService;
import com.example.mummoomserver.login.users.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static com.example.mummoomserver.config.resTemplate.ResponseTemplateStatus.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;
    private final UserService userService;

    @ApiOperation(value="게시글 등록", notes="게시글을 등록합니다. JWT 토큰 입력 필수! 반환값(data) : postIdx")
    @PostMapping("/post")
    @ApiResponses({
     @ApiResponse(code=3000, message="데이터베이스 요청 에러.")
            ,@ApiResponse(code=8001, message="회원정보를 찾을 수 없습니다.")
            ,@ApiResponse(code=8005, message="제목을 입력해주세요.")
            ,@ApiResponse(code=8006, message="내용을 입력해주세요.")
    })
    public ResponseTemplate<Long> save(@RequestBody PostSaveRequestDto requestDto) {
        if(requestDto.getTitle()==null)return new ResponseTemplate<>(EMPTY_TITLE);
        if(requestDto.getContent()==null)return new ResponseTemplate<>(EMPTY_CONTENT);
        try{
            String email = userService.getAuthUserEmail();
            Long result = postService.save(email, requestDto);
            return new ResponseTemplate<>(result);
        } catch (ResponeException e){
            return new ResponseTemplate<>(e.getStatus());
        }
    }

    @ApiOperation(value="게시글 수정", notes="게시글을 수정합니다. JWT 토큰 입력 필수!")
    @ApiImplicitParam(name="postIdx", value="게시글 식별자")
    @PatchMapping("/post/{postIdx}")
    @ApiResponses({
            @ApiResponse(code=8000, message="존재하지 않는 게시글 입니다.")
            ,@ApiResponse(code=8001, message="회원정보를 찾을 수 없습니다.")
            ,@ApiResponse(code=8004, message="작성자만 사용할 수 있습니다.")
            ,@ApiResponse(code=8005, message="제목을 입력해주세요.")
            ,@ApiResponse(code=8006, message="내용을 입력해주세요.")
    })
    public ResponseTemplate<String> update(@PathVariable Long postIdx, @RequestBody PostUpdateRequestDto requestDto){
        if(requestDto.getTitle()==null)return new ResponseTemplate<>(EMPTY_TITLE);
        if(requestDto.getContent()==null)return new ResponseTemplate<>(EMPTY_CONTENT);
        try {
            String email = userService.getAuthUserEmail();
            String result = "게시글 수정에 성공했습니다.";
            postService.update(postIdx, email, requestDto);
            return new ResponseTemplate<>(result);
        } catch (ResponeException e){
            return new ResponseTemplate<>(e.getStatus());
        }
    }

    @ApiOperation(value="게시글 상세 정보 조회", notes="게시글 상세 정보를 조회합니다. JWT 토큰 입력 필수!")
    @GetMapping("/post/{postIdx}")
    @ApiResponses({
            @ApiResponse(code=8000, message="존재하지 않는 게시글 입니다.")
            ,@ApiResponse(code=8001, message="회원정보를 찾을 수 없습니다.")
    })
    public ResponseTemplate<PostIdxResponseDto> findByPostIdx(@PathVariable Long postIdx){
        try {
            String email = userService.getAuthUserEmail();
            PostIdxResponseDto result = postService.findByPostIdx(email, postIdx);
            return new ResponseTemplate<>(result);
        } catch (ResponeException e){
            return new ResponseTemplate<>(e.getStatus());
        }
    }

    @ApiOperation(value="게시글 전체 조회", notes="전체 게시글을 조회합니다.")
    @GetMapping("/posts")
    @ApiResponses({
            @ApiResponse(code=3000, message="데이터베이스 요청 에러.")
    })
    public ResponseTemplate<List<PostResponseDto>> getPosts(){
        try{
            return new ResponseTemplate<>(postService.getPosts());
        } catch (ResponeException e){
            return new ResponseTemplate<>(e.getStatus());
        }
    }

    @ApiOperation(value="게시글 삭제", notes="게시글을 삭제합니다. JWT 토큰 입력 필수!")
    @DeleteMapping("/post/{postIdx}")
    @ApiResponses({
            @ApiResponse(code=8000, message="존재하지 않는 게시글 입니다.")
            ,@ApiResponse(code=8001, message="회원정보를 찾을 수 없습니다.")
            ,@ApiResponse(code=8004, message="작성자만 사용할 수 있습니다.")
    })
    public ResponseTemplate<String> delete(@PathVariable Long postIdx){
        try{
            String email = userService.getAuthUserEmail();
            postService.delete(email, postIdx);
            String result = "게시글 삭제에 성공했습니다.";
            return new ResponseTemplate<>(result);
        } catch(ResponeException e){
            return new ResponseTemplate<>(e.getStatus());
        }
    }

    @ApiOperation(value="내가 쓴 게시글 조회", notes="내가 쓴 게시글을 조회합니다. JWT 토큰 입력 필수!")
    @GetMapping("/post/findMyPosts")
    @ApiResponses({
            @ApiResponse(code=3000, message="데이터베이스 요청 에러.")
            ,@ApiResponse(code=8001, message="회원정보를 찾을 수 없습니다.")
    })
    public ResponseTemplate<List<PostResponseDto>> findMyPost(){
        try{
            String email = userService.getAuthUserEmail();
            return new ResponseTemplate<>(postService.findMyPost(email));
        }catch(ResponeException e){
            return new ResponseTemplate<>(e.getStatus());
        }
    }

    @ApiOperation(value="좋아요한 게시글 조회", notes="좋아요한 게시글을 조회합니다. JWT 토큰 입력 필수!")
    @GetMapping("/post/findMyLikes")
    @ApiResponses({
            @ApiResponse(code=3000, message="데이터베이스 요청 에러.")
            ,@ApiResponse(code=8001, message="회원정보를 찾을 수 없습니다.")
    })
    public ResponseTemplate<List<PostResponseDto>> findMyLikes(){
        try{
            String email = userService.getAuthUserEmail();
            return new ResponseTemplate<>(postService.findMyLikes(email));
        }catch(ResponeException e){
            return new ResponseTemplate<>(e.getStatus());
        }
    }

}
