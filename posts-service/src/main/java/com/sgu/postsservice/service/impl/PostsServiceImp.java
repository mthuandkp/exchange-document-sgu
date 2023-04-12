package com.sgu.postsservice.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sgu.postsservice.constant.Constant;
import com.sgu.postsservice.constant.PostStatus;
import com.sgu.postsservice.dto.request.*;
import com.sgu.postsservice.dto.response.*;
import com.sgu.postsservice.exception.*;
import com.sgu.postsservice.model.Category;
import com.sgu.postsservice.model.Posts;
import com.sgu.postsservice.repository.PostsRepository;
import com.sgu.postsservice.service.CloudinaryService;
import com.sgu.postsservice.service.PostsService;
import com.sgu.postsservice.utils.DateUtils;
import com.sgu.postsservice.utils.StringUtils;
import org.bson.types.ObjectId;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class PostsServiceImp implements PostsService {
    @Autowired
    private WebClient webClient;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private CloudinaryService cloudinaryService;


    @Override
    public HttpResponseEntity createPosts(String token, PostRequest postRequest) {
        HttpResponseEntity getCategory = null;
        Category categoryEntity = null;

        if(!token.startsWith("Bearer ")){
            throw new BadRequestException("Token không hợp lệ");
        }

        DecodedJWT jwt = JWT.decode(token.substring("Bearer ".length()));
        String username = jwt.getSubject();
        String userId;




        try {
            HttpResponseEntity httpResponseEntity = webClient.get()
                    .uri("http://localhost:8082/api/v1/profile/username/" + username)
                    .retrieve()
                    .bodyToMono(HttpResponseEntity.class)
                    .block();

            String jsonInString = new Gson().toJson(httpResponseEntity.getData().get(0));
            JSONObject jsonObject = new JSONObject(jsonInString);
            userId = jsonObject.getString("id");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new NotFoundException("Tài khoản không tồn tại");
        }

        try {
            getCategory = webClient.get()
                    .uri("http://localhost:8083/api/v1/categories/slug/" + postRequest.getCategorySlug())
                    .retrieve()
                    .bodyToMono(HttpResponseEntity.class)
                    .block();

            String jsonInString = new Gson().toJson(getCategory.getData().get(0));
            JSONObject mJSONObject = new JSONObject(jsonInString);
            categoryEntity = Category.builder()
                    .id(mJSONObject.getString("id"))
                    .name(mJSONObject.getString("name"))
                    .url(mJSONObject.getString("url"))
                    .categorySlug(mJSONObject.getString("categorySlug"))
                    .build();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new NotFoundException("Danh mục không tồn tại");
        }


        Posts postsEntity = convertToEntity(postRequest);
        postsEntity.setAccountId(new ObjectId(userId));

        postsEntity.setCategory(categoryEntity);
        postsRepository.save(postsEntity);


        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo thành công")
                .build();
        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity getAll(
            Boolean getHidden,
            String slug,
            String keyword,
            String date,
            Integer page,
            Integer size,
            Long startPrice,
            Long endPrice) {
        List<Posts> postsList = postsRepository.findAll();

        if (getHidden == false) {
            postsList = postsList.stream()
                    .filter(posts -> posts.getPostStatus().equals(PostStatus.DISPLAY)
                    )
                    .collect(Collectors.toList());
        }
        if (!slug.equals("")) {
            postsList = postsList.stream()
                    .filter(posts -> posts.getCategory().getCategorySlug().equals(slug.replaceAll(" ", "")))
                    .collect(Collectors.toList());
        }
        postsList = this.searchMultipleCondition(postsList, date, keyword);

        if (startPrice != null) {
            postsList = postsList.stream().filter(posts -> posts.getPrice() >= startPrice)
                    .collect(Collectors.toList());
        }

        if (endPrice != null) {
            postsList = postsList.stream().filter(posts -> posts.getPrice() <= endPrice)
                    .collect(Collectors.toList());
        }

        Pagination pagination = null;

        if (page != null && size != null) {
            //Pagination
            int to = page * size;
            int from = to - size;
            int totalPage = postsList.size() % size == 0 ?
                    postsList.size() / size : postsList.size() / size + 1;

            List<Posts> paginationList = new ArrayList<>();
            for (int i = from; i < to; i++) {
                if (i < postsList.size()) {
                    paginationList.add(postsList.get(i));
                }
            }

            pagination = Pagination.builder()
                    .page(page)
                    .size(size)
                    .total_page(totalPage)
                    .total_size(postsList.size())
                    .build();

            postsList = paginationList;
        }


        List<PostsResponse> postsResponseList = postsList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());


        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                postsResponseList,
                pagination
        );

        return httpResponseEntity;
    }


    private List<Posts> searchMultipleCondition(List<Posts> postsList, String isNewest, String keyword) {
        //divide post base on priority
        if (keyword != null) {
            List<String> keywordList = Arrays.asList(keyword.split(" "))
                    .stream()
                    .map(str -> StringUtils.convertTextToEnglish(str))
                    .collect(Collectors.toList());
            TreeMap<Integer, List<Posts>> treeMap = new TreeMap<>(Collections.reverseOrder());
            for (Posts p : postsList) {
                int priority = Posts.calcPriority(p.getTitle(), keywordList);
                if(priority == 0){
                    continue;
                }

                if (treeMap.containsKey(priority)) {
                    List<Posts> treeListData = new ArrayList<>(treeMap.get(priority));
                    treeListData.add(p);
                    treeMap.put(priority, treeListData);
                } else {
                    treeMap.put(priority, Arrays.asList(p));
                }
            }

            //Sort each element in treemap
            for (Map.Entry<Integer, List<Posts>> entry : treeMap.entrySet()) {
                List<Posts> postsListTreeMapItem = new ArrayList<>(entry.getValue());
                Collections.sort(postsListTreeMapItem, (p1, p2) -> {
                    return p1.compareTime(p2, isNewest);
                });
                treeMap.put(entry.getKey(), postsListTreeMapItem);
            }

            //Convert treemap to arraylist
            List<Posts> sortedList = new ArrayList<>();

            for (Map.Entry<Integer, List<Posts>> entry : treeMap.entrySet()) {
                sortedList.addAll(new ArrayList<>(entry.getValue()));
            }

            postsList = sortedList;
        } else {
            Collections.sort(postsList, (p1, p2) -> {
                return p1.compareTime(p2, isNewest);
            });
        }

        return postsList;
    }


    @Override
    public HttpResponseEntity updatePosts(String id, PostUpdateRequest postUpdateRequest) {
        Posts posts = postsRepository.findById(new ObjectId(id)).orElseThrow(
                () -> new NotFoundException(String.format("Không tìm thấy bài viết có id=%s", id))
        );

        try {
            HttpResponseEntity getCategory = webClient.get()
                    .uri("http://localhost:8083/api/v1/categories/slug/" + postUpdateRequest.getCategorySlug())
                    .retrieve()
                    .bodyToMono(HttpResponseEntity.class)
                    .block();
        } catch (Exception ex) {
            throw new NotFoundException("Danh mục không tồn tại");
        }

        Posts savePosts = Posts.builder()
                .id(posts.getId())
                .accountId(posts.getAccountId())
                .title(postUpdateRequest.getTitle())
                .description(postUpdateRequest.getDescription())
                .price(postUpdateRequest.getPrice())
                .postsSlug(StringUtils.createSlug(postUpdateRequest.getTitle() + "-" + DateUtils.getNow()))
                .postStatus(posts.getPostStatus())
                .reasonBlock(posts.getReasonBlock())
                .thumbnail(postUpdateRequest.getImageList().get(0))
                .createdAt(posts.getCreatedAt())
                .updatedAt(DateUtils.getNow())
                .category(posts.getCategory())
                .postsImageList(postUpdateRequest.getImageList())
                .build();

        savePosts = postsRepository.save(savePosts);
        System.out.println(savePosts);
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Sửa thành công")
                .build();

        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity uploadMultiFiles(MultipartFile[] files) {
        List<String> list = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String contentType = file.getContentType();
                Predicate<String> checkFileExtension = s -> {
                    return !contentType.equals("image/jpeg") && !contentType.equals("image/png");
                };
                if (checkFileExtension.test(contentType)) {
                    throw new BadRequestException(String.format("Chỉ chấp nhận file png,jpg,jpeg"));
                }

                Map<?, ?> map = cloudinaryService.upload(file, "posts/");
                list.add((String) map.get("url"));

            } catch (InternalServerException e) {
                throw new ServerInternalException(String.format("Lỗi khi upload nhiều file %s", e.getMessage()));
            }
        }
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                list,
                null
        );

        return httpResponseEntity;
    }

    //
//    @Override
//    public HttpResponseEntity getById(Long id) {
//        Posts posts  =postsRepository.findById(id).orElseThrow(
//                ()->new NotFoundException(String.format("Bài viết với id=%s không tồn tại",id))
//        );
//        PostsResponse postsResponse = this.convertToResponse(posts);
//        List<PostsResponse> postsResponseList = Arrays.asList(postsResponse);
//
//        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
//                HttpStatus.OK.value(),
//                Constant.SUCCESS,
//                postsResponseList,
//                null
//        );
//
//        return httpResponseEntity;
//    }
//
//    @Override
//    public HttpResponseEntity getByAccountId(Long id) {
//        List<Posts> postsList = postsRepository.getByAccountId(id);
//        List<PostsResponse> postsResponseList = postsList.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
//                HttpStatus.OK.value(),
//                Constant.SUCCESS,
//                postsResponseList,
//                null
//        );
//
//        return httpResponseEntity;
//    }
//
//    @Override
//    public HttpResponseEntity getByCategoryId(Long id) {
//        List<Posts> postsList = postsRepository.getByCategoryId(id);
//        List<PostsResponse> postsResponseList = postsList.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
//                HttpStatus.OK.value(),
//                Constant.SUCCESS,
//                postsResponseList,
//                null
//        );
//
//        return httpResponseEntity;
//    }
//
    @Override
    public HttpResponseEntity userHidden(String id, PostsHiddenRequest postsHiddenRequest, String token) {
        Posts posts = postsRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new NotFoundException(String
                        .format("Bài viết với id=%s không tồn tại", id)));

        try {

            if(!token.startsWith("Bearer ")){
                throw new BadRequestException("Token không hợp lệ");
            }

            DecodedJWT jwt = JWT.decode(token.substring("Bearer ".length()));
            String username = jwt.getSubject();
            HttpResponseEntity httpResponseEntity = webClient.get()
                    .uri("http://localhost:8082/api/v1/profile/username/" + username)
                    .retrieve()
                    .bodyToMono(HttpResponseEntity.class)
                    .block();

            String json = new Gson().toJson(httpResponseEntity.getData().get(0));
            JsonObject jsonObject = (new JsonParser()).parse(json).getAsJsonObject();

            String idUserToken = jsonObject.get("id").getAsString();


            if(!posts.getAccountId().toString().equals(idUserToken)){
                throw new UnAuthorizationException("Người dùng không phải là chủ bài viết");
            }

        }catch (UnAuthorizationException ex){
            throw new UnAuthorizationException(ex.getMessage());
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new NotFoundException("Danh mục không tồn tại");
        }

        if (posts.getPostStatus().equals(PostStatus.USER_HIDDEN)) {
            throw new NoContentException("Bài viết đã ẩn trước đó");
        }

        if (posts.getPostStatus().equals(PostStatus.USER_DELETE) ||
                posts.getPostStatus().equals(PostStatus.ADMIN_DELETE)) {
            throw new NoContentException("Bài viết đã bị xoá trước đó. Lý do xoá: " + posts.getReasonBlock());
        }

        if (posts.getPostStatus().equals(PostStatus.ADMIN_HIDDEN)) {
            throw new NoContentException("Bài viết đã bị quản trị viên ẩn. Lý do : " + posts.getReasonBlock());
        }


        posts = posts.toBuilder()
                .postStatus(PostStatus.USER_HIDDEN)
                .reasonBlock(postsHiddenRequest.getReasonBlock())
                .updatedAt(DateUtils.getNow())
                .build();


        posts = postsRepository.save(posts);
        List<PostsResponse> postsResponseList = Arrays.asList(this.convertToResponse(posts));
        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Ẩn bài viết thành công")
                .build();

        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity userUnhidden(String id,String token) {
        Posts posts = postsRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new NotFoundException(String
                        .format("Bài viết với id=%s không tồn tại", id)));

        try {

            if(!token.startsWith("Bearer ")){
                throw new BadRequestException("Token không hợp lệ");
            }

            DecodedJWT jwt = JWT.decode(token.substring("Bearer ".length()));
            String username = jwt.getSubject();
            HttpResponseEntity httpResponseEntity = webClient.get()
                    .uri("http://localhost:8082/api/v1/profile/username/" + username)
                    .retrieve()
                    .bodyToMono(HttpResponseEntity.class)
                    .block();

            String json = new Gson().toJson(httpResponseEntity.getData().get(0));
            JsonObject jsonObject = (new JsonParser()).parse(json).getAsJsonObject();

            String idUserToken = jsonObject.get("id").getAsString();


            if(!posts.getAccountId().toString().equals(idUserToken)){
                throw new UnAuthorizationException("Người dùng không phải là chủ bài viết");
            }

        }catch (UnAuthorizationException ex){
            throw new UnAuthorizationException(ex.getMessage());
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new NotFoundException("Danh mục không tồn tại");
        }





        if (posts.getPostStatus().equals(PostStatus.USER_DELETE) ||
                posts.getPostStatus().equals(PostStatus.ADMIN_DELETE)) {
            throw new NoContentException("Bài viết đã bị xoá. Lý do xoá: " + posts.getReasonBlock());
        }

        if (posts.getPostStatus().equals(PostStatus.DISPLAY)) {
            throw new NoContentException("Bài viết chưa được ẩn");
        }


        posts.setPostStatus(PostStatus.DISPLAY);
        posts.setReasonBlock("");
        posts = postsRepository.save(posts);
        List<PostsResponse> postsResponseList = Arrays.asList(this.convertToResponse(posts));

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Bỏ ẩn bài viết thành công")
                .build();


        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity userDelete(String slug, PostsDeleteRequest postsDeleteRequest, String token) {
        Posts posts = postsRepository.findByPostsSlug(slug)
                .orElseThrow(() -> new NotFoundException(""));

        try {
            if(!token.startsWith("Bearer ")){
                throw new BadRequestException("Token không hợp lệ");
            }

            DecodedJWT jwt = JWT.decode(token.substring("Bearer ".length()));
            String username = jwt.getSubject();
            HttpResponseEntity httpResponseEntity = webClient.get()
                    .uri("http://localhost:8082/api/v1/profile/username/" + username)
                    .retrieve()
                    .bodyToMono(HttpResponseEntity.class)
                    .block();

            String json = new Gson().toJson(httpResponseEntity.getData().get(0));
            JsonObject jsonObject = (new JsonParser()).parse(json).getAsJsonObject();

            String idUserToken = jsonObject.get("id").getAsString();

            if(!posts.getAccountId().toString().equals(idUserToken)){
                throw new UnAuthorizationException("Người dùng không phải là chủ bài viết");
            }

        }catch (UnAuthorizationException ex){
            throw new UnAuthorizationException(ex.getMessage());
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new NotFoundException("Danh mục không tồn tại");
        }



        if (posts.getPostStatus().equals(PostStatus.USER_DELETE) ||
                posts.getPostStatus().equals(PostStatus.ADMIN_DELETE)) {
            throw new NoContentException("Bài viết đã bị xoá trước đó. Lý do xoá: " + posts.getReasonBlock());
        }

        posts.setPostStatus(PostStatus.USER_DELETE);
        posts.setReasonBlock(postsDeleteRequest.getReasonDelete());
        posts = postsRepository.save(posts);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Xoá bài viết thành công")
                .build();

        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity adminDelete(String slug, PostsDeleteRequest postsDeleteRequest) {
        Posts posts = postsRepository.findByPostsSlug(slug)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài viết"));

        if (posts.getPostStatus().equals(PostStatus.USER_DELETE) ||
                posts.getPostStatus().equals(PostStatus.ADMIN_DELETE)) {
            throw new NoContentException("Bài viết đã bị xoá. Lý do xoá: " + posts.getReasonBlock());
        }

        posts.setPostStatus(PostStatus.ADMIN_DELETE);
        posts.setReasonBlock(postsDeleteRequest.getReasonDelete());
        posts = postsRepository.save(posts);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.builder()
                .code(HttpStatus.OK.value())
                .message("Xoá bài viết thành công")
                .build();

        return httpResponseEntity;
    }

    //
//    @Override
//    public HttpResponseEntity getByCategorySlug(String slug, int page, int size) {
//        Pageable pageable = PageRequest.of(page-1,size);
//        Category category = categoryRepository.findBySlug(slug)
//                .orElseThrow(()->new NotFoundException(
//                        String.format("Không thể tìm thấy slug=%s",slug)));
//        Page<Posts> postsPage = postsRepository.findAllByCategoryId(category.getId(),pageable);
//        List<PostsResponse> postsResponseList = postsPage.getContent()
//                .stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//
//        Pagination pagination = Pagination.builder()
//                .page(page)
//                .size(size)
//                .total_page(postsPage.getTotalPages())
//                .total_size(postsPage.getTotalElements())
//                .build();
//        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
//                HttpStatus.OK.value(),
//                Constant.SUCCESS,
//                postsResponseList,
//                pagination
//        );
//        return httpResponseEntity;
//    }
//
    @Override
    public HttpResponseEntity getBySlug(String slug) {
        Posts posts = postsRepository.getByPostsSlug(slug).orElseThrow(
                () -> new NotFoundException(String.format("Không tìm thấy bài viết"))
        );

        List<PostsResponse> postsResponseList = Arrays.asList(this.convertToResponse(posts));

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                postsResponseList,
                null
        );
        return httpResponseEntity;

    }

    @Override
    public HttpResponseEntity getByUserId(String id) {
        List<Posts> postsList = postsRepository.getByAccountId(new ObjectId(id));

        List<PostsResponse> postsResponseList = postsList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                postsResponseList,
                null
        );
        return httpResponseEntity;
    }

    @Override
    public HttpResponseEntity getById(String id) {
        Posts posts = postsRepository.findById(new ObjectId(id)).orElseThrow(()->{
            throw new NotFoundException("Không tìm thấy bài viết");
        });

        PostsResponse postsResponse = this.convertToResponse(posts);
        List<PostsResponse> postsResponseList = Arrays.asList(postsResponse);

        HttpResponseEntity httpResponseEntity = HttpResponseEntity.convertToResponeEntity(
                HttpStatus.OK.value(),
                Constant.SUCCESS,
                postsResponseList,
                null
        );
        return httpResponseEntity;
    }
//
//    private int calcPriority(String title, List<String> keywordList) {
//        int priority = 0;
//        String encodeTitle = StringUtils.convertTextToEnglish(title);
//        for(String key: keywordList){
//            if(encodeTitle.contains(key)){
//                priority++;
//            }
//        }
//
//        return priority;
//    }

    private PostsResponse convertToResponse(Posts posts) {
        UserResponse userResponse = null;
        try {
            HttpResponseEntity httpResponseEntityPosts = webClient.get()
                    .uri("http://localhost:8082/api/v1/profile/" + posts.getAccountId().toString())
                    .retrieve()
                    .bodyToMono(HttpResponseEntity.class)
                    .block();

            String json = new Gson().toJson(httpResponseEntityPosts.getData().get(0));
            JsonObject jsonObject = (new JsonParser()).parse(json).getAsJsonObject();

            //accountAddress = (jsonObject.get("address").getAsString());

            userResponse = userResponse.builder()
                    .id(jsonObject.get("id").getAsString())
                    .username(jsonObject.get("username").getAsString())
                    .avatar(jsonObject.get("avatar").getAsString())
                    .name(jsonObject.get("name").getAsString())
                    .address(jsonObject.get("address").getAsString())
                    .phone(jsonObject.get("phone").getAsString())
                    .birthday(jsonObject.get("birthday").getAsString())
                    .gender(jsonObject.get("gender").getAsBoolean())
                    .build();


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        return PostsResponse.builder()
                .id(posts.getId().toString())
                .accountId(posts.getAccountId().toString())
                .userProfile(userResponse)
                .title(posts.getTitle())
                .description(posts.getDescription())
                .price(posts.getPrice())
                .postSlug(posts.getPostsSlug())
                .postStatus(posts.getPostStatus())
                .reasonBlock(posts.getReasonBlock())
                .thumbnail(posts.getThumbnail())
                .createdAt(posts.getCreatedAt())
                .updatedAt(posts.getUpdatedAt())
                .category(this.convertToCategoryResponse(posts.getCategory()))
                .postsImageList(posts.getPostsImageList())
                .build();
    }

    private CategoryResponse convertToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId().toString())
                .name(category.getName())
                .url(category.getUrl())
                .categorySlug(category.getCategorySlug())
                .build();
    }

    private Posts convertToEntity(PostRequest postRequest) {
        return Posts.builder()
                .title(postRequest.getTitle())
                .description(postRequest.getDescription())
                .price(postRequest.getPrice())
                .postsSlug(StringUtils.createSlug(postRequest.getTitle() + "-" + DateUtils.getNow()))
                .postsImageList(postRequest.getImageList())
                .thumbnail(postRequest.getImageList().get(0))
                .build();
    }
}
