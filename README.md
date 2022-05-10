> ## 과제 내용

## REST API 기반의 게시판 기능 구현 -> 화면 없이 API를 통한 기능적 구현 

### ▶︎ 개발 요구사항

✔️ 기술 스택

- Spring boot

- Kotlin

- MariaDB (or MySql)

  

**✔️ 기능 요구사항**

- 회원가입 가능(회원 정보에 로그인 아이디/ 비밀번호/ 별칭 필수 포함)
- 회원가입한 정보로 로그인이 가능
- 로그인한 회원은 게시판에 글을 작성
- 로그인한 회원은 본인이 작성한 글을 본인만을 볼 수 있게 잠금설정
- 로그인한 회원은 본인이 게시판 글을 수정
- 로그인한 회원은 본인이 작성한 게시판 글을 삭제
- 로그인한 회원만 게시판에 접근
- (이외 개발자가 생각 하는 기능 추가)



> ## 구현 기능

**✔️ 기술 스택**

- Spring boot
- MariaDB



**✔️ 기능**

#### 회원가입 수정/로그인

 - Remember Me (로그인한 유저만 기억)
 - 로그인한 사람만 회원수정 닉네임변경 가능
 - User/Admin 으로 사용자 권한 분리

### 로그인

- 로그인은 Spring Security를 이용하여 시큐리티가 대신 로그인을 수행할 수 있도록 하고 유저 정보는

   

  ```
  UserDetails 인터페이스
  ```

  를 상속받아 구현하였습니다.

  - `user-login.html`의 form 태그에 `action`과 `method`에 post로 설정합니다.
  - input 태그에는 `name` 값을 줍니다.

- 실제 로그인한 유저는 `UserDetails`를 상속받은 `PrincipalDetail` 클래스에 사용자 정보가 담겨 있고, 사용자 정보를 가져오는 `UserDetailsService 인터페이스`를 상속받은 `PrincipalDetailService` 클래스를 생성하였습니다.

- 회원 수정의 경우 변경 완료시킨 후 다시 회원 수정으로 들어갔을 때 바꿔야하므로 이 때 스프링 시큐리티 세션을 이용하여 반영하였습니다.

  - @AuthenticationPrincipal에서 회원정보를 파라미터로 받고 회원수정 로직에서 setter를 통해 변경시켰습니다.



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //sequence, auto_increment
    
    @Column(nullable = false, length = 50, unique = true)
    private String username; //아이디
    
    @Column(nullable = false, length = 100)
    private String password;
    
    @Column(nullable = false, length = 30)
    private String email;
    
    @Column(nullable = false, length = 20)
    private String nickname; //닉네임
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column
    private String provider;
    
    @Column
    private String providerId;

- username : 회원ID (다른 회원과 겹치면 안되기 때문에 unique 값을 주었습니다.)
- Role : 권한
  - 기본적으로 회원가입을 한 모든 사용자는 `Role.USER`을 갖고 이 사이트의 모든 컨텐츠를 이용할 수 있게 만들었습니다.
  - 따로 ADMIN 페이지를 구현하지 못했습니다.
- provider, providerId : 소셜로그인을 한 경우와 일반 회원가입을 한 경우를 구분하기 위해 추가하였습니다.
- 생성시간/수정시간은 JPA Auditing을 이용하여 테이블마다 자동화하였습니다.
- 프론트단에서 제약조건(ex. 아이디 4자 이상 등등)을 걸어두었습니다.



#### 게시판 CRUD

- 게시판 조회수
- 게시판 페이징
- 게시판 검색
- 게시판 댓글 등록 및 삭제
  - 자신이 작성한 댓글만 삭제 가능

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Lob
    private String content;
    
    @Column
    private int count; //조회수
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;
    
    @OrderBy("id desc")
    @JsonIgnoreProperties({"board"})
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Reply> replyList;

- @ManyToOne(fetch = FetchType.EAGER)
  - 어떤 사용자가 게시글을 작성했는지 알기 위해 User 테이블을 조인해야 하는데 이 때 연관관계를 한 명의 사용자가 많은 게시글을 작성할 수 있으므로 @ManyToOne으로 설정합니다. (이 때 디폴트 FetchType은 Eager입니다.)
- @JoinColumn(name = "userId") : FK 컬럼명을 userId로 설정합니다.
- private List<Reply> replyList;
  - @OrderBy("id desc") : 댓글 작성시 최근 댓글이 위로 올라오도록 설정합니다.
  - @JsonIgnoreProperties({"board"}) : Board를 조회하게 되면 Reply 객체를 조회하게 되는데 이 때 Reply 엔티티에는 또 Board 객체를 조회하게 됩니다. 이러면 무한 반복이 일어나기 때문에 한번만 조회하게 설정할 수 있게 @JsonIgnoreProperties를 사용합니다.
  - Reply 테이블에는 외래키가 잡혀있어서 실제로 삭제가 동작안하는 문제가 발생하는데 이 때 `cascade = CascadeType.REMOVE)` 옵션을 주면 외래키가 있어도 삭제가 완료됩니다.



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "boardId")
    private Board board;
    
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

- 누가 댓글을 작성했는지와 어느 게시글에 작성했는지 알기 위해 FK 설정을 합니다. (boardId, userId )



### 자신만 수정, 삭제 가능하도록 설정

- implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity5' 의존성을 다운받으면
  - th:value="${#authentication.principal.email}" 이런식으로 사용자 정보를 뷰에 뿌려줄 수 있습니다.
- 본인이 작성한 게시글 혹은 댓글에 대한 수정, 삭제만 가능하도록 설정하였습니다.



### 페이징 / 검색

    private final BoardService boardService;
    
    @GetMapping("/")
    public String index(Model model,
                        @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam(required = false, defaultValue = "") String search) {
        Page<Board> boards = boardService.findByTitleContainingOrContentContaining(search, search, pageable);
        int startPage = Math.max(1, boards.getPageable().getPageNumber() - 4);
        int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("boards", boards);
        return "index";
    }

- @PageableDefault : 페이지의 사이즈, 정렬을 설정하기 위해 사용하였습니다.
- 검색같은 경우는 주소에 쿼리스트링으로 받아서 처리하는데 @RequestParam를 사용하였고, null값이 들어갈 수도 있으므로 속성값을 설정하였습니다.
- findByTitleContainingOrContentContaining : JPA에서 Containing을 사용하게 되면 `LIKE문`처럼 동작하게 됩니다.
- 페이지 목록에서 시작 페이지 번호(startPage)와 끝 페이지 번호(endPage)를 선언하여 모델에다가 넘겨주었습니다.
