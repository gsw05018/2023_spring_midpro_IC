// 로딩 팝업 창
window.onload = function () {
    var modal = document.getElementById("imageModal");
    var closeModal = document.getElementById("closeModal");

    // 모달 닫기 함수
    function closeModalFunc() {
        modal.style.display = "none";
    }

    // "닫기" 아이콘 클릭 시 모달 닫기
    closeModal.addEventListener("click", closeModalFunc);

    // 모달 외부 클릭 시 모달 닫기
    window.addEventListener("click", function (event) {
        if (event.target == modal) {
            closeModalFunc();
        }
    });

    // 페이지 로드 시 모달 열기
    modal.style.display = "block";
};


// 로딩 팝업 창 끝


// 로그인 메뉴
$(document).ready(function () {
    $(
        ".wrap-box >  .top-bar-box > .login-box > .lo-box > a"
    ).click(function () {
        $(this).siblings(".toggle-menu").toggleClass("active");
    });
});
// 로그인메뉴 끝

// 슬릭 슬라이더 옵션추가
$(document).ready(function () {
    $(".slick-wrap").slick({
        autoplay: true, // 자동 슬라이드
        autoplaySpeed: 2000, // 자동슬라이드 넘어가지는 속도 (ms)
        arrows: true, // 화살표 만들기.
        dots: true, // 하단 닷츠 네비게이션 활성화
        draggable: true, // 드래그로 슬라이드변경 허용 여부
        fade: false, // fade in , fade out 활성화
        infinite: true, // 무한이 옆으로 흐르게 만드는 슬라이드
        initialSlide: 0, // 처음에 활성화 될 슬라이드 번호 (인덱스 번호로)
        prevArrow: $(".left-icon"),
        nextArrow: $(".right-icon"),
        pauseOnHover: true, //마우스가 올라갔을 때 이동 여부
        pauseOnDotsHover: true, // 마우스가 닷츠에 올라갓을때 이동 여부
        cssEase: "linear", // transition-timing-function 적용하기 큐빅배지어도가능
        speed: 300, //슬라이드가 넘어가지는 속도 (ms)
        centerMode: true, // 센터 모드 활성화
        centerPadding: "250px", // 중앙 슬라이드와 주변 슬라이드 사이의 간격 (원하는 값으로 조정)
        draggable: true, //드래그 가능 여부
        slidesToShow: 1 // 동시에 보여질 슬라이드 수 (롯데렌터카와 동일하게 조정)
    });
});
// 슬릭 슬라이더 끝

// 이미지 호버 js
$(".hover").mouseleave(function () {
    $(this).removeClass("hover");
});
// 이미지 호버 끝


//   하단 배너 슬릭 슬라이드
$(document).ready(function () {
    $(".slider-wrap-2").slick({
        autoplay: true, // 자동 슬라이드
        autoplaySpeed: 2000, // 자동슬라이드 넘어가지는 속도 (ms)
        dots: true, // 하단 닷츠 네비게이션 활성화
        draggable: true, // 드래그로 슬라이드변경 허용 여부
        infinite: true, // 무한이 옆으로 흐르게 만드는 슬라이드
        initialSlide: 0, // 처음에 활성화 될 슬라이드 번호 (인덱스 번호로)
        pauseOnDotsHover: true, // 마우스가 닷츠에 올라갓을때 이동 여부
        cssEase: "linear", // transition-timing-function 적용하기 큐빅배지어도가능
        speed: 300, //슬라이드가 넘어가지는 속도 (ms)
        draggable: false //드래그 가능 여부
    });
});
//   하단 배너 슬릭 슬라이드 끝

// 억새축제 슬라이드
$(document).ready(function () {
    $(".se-img-box").slick({
        // 여기에 슬라이더 설정을 추가하세요.
        dots: false, // 점 네비게이션 표시
        autoplay: true, // 자동 재생 활성화
        autoplaySpeed: 2000, // 자동 재생 속도 (2초마다 변경)
        infinite : true,
        speed : 200,
    });
});
// 억새축제 슬라이드 끝

// 올림버튼
$(window).scroll(function () {
    var $scrollTop = $(this).scrollTop();
    if ($scrollTop > 0) {
        $(".goto-tp-btn").css({ opacity: 1 }, 300);
    } else {
        $(".goto-tp-btn").css({ opacity: 0 }, 300);
    }
});

$(document).ready(function () {
    $(".goto-tp-btn").click(function () {
        $("html, body").animate({ scrollTop: 0 });
    });
});
// 올림버튼 끝

// toastr 옵션
 toastr.options = {
     closeButton: true,
     debug: false,
     newestOnTop: true,
     progressBar: true,
     positionClass: "toast-top-right",
     preventDuplicates: false,
     onclick: null,
     showDuration: "300",
     hideDuration: "1000",
     timeOut: "5000",
     extendedTimeOut: "1000",
     showEasing: "swing",
     hideEasing: "linear",
     showMethod: "fadeIn",
     hideMethod: "fadeOut"
 };

 function parseMsg(msg) {
     const [pureMsg, ttl] = msg.split(";ttl=");

     const currentJsUnixTimestamp = new Date().getTime();

     if (ttl && parseInt(ttl) + 5000 < currentJsUnixTimestamp) {
         return [pureMsg, false];
     }

     return [pureMsg, true];
 }

 function toastMsg(isNotice, msg) {
     if (isNotice) toastNotice(msg);
     else toastWarning(msg);
 }

 function toastNotice(msg) {
     const [pureMsg, needToShow] = parseMsg(msg);

     if (needToShow) {
         toastr["success"](pureMsg, "알림");
     }
 }

 function toastWarning(msg) {
     const [pureMsg, needToShow] = parseMsg(msg);

     if (needToShow) {
         toastr["warning"](pureMsg, "경고");
     }
 }

 // 어떠한 기능을 살짝 늦게(0.1 초 미만)
 function setTimeoutZero(callback) {
     setTimeout(callback);
 }

 $(function () {
     $('select[value]').each(function (index, el) {
         const value = $(el).attr('value');
         if ( value ) $(el).val(value);
     });
 });

 $('a[method="post"]').click(function (e){
    let onclickAfter = null;

    eval("onclickAfter = function(){ " + $(this).attr('onclick-after') + "}");

    if (!onclickAfter()) return false;

    const action = $(this).attr('href');
    const csfTokenValue = $("meta[name='_csrf']").attr("content");

    const $form = $(`<form action="${action}" method="POST"><input type="hidden" name="_csrf" value="${csfTokenValue}"></form>`);
    $('body').append($form);
    $form.submit();

    return false;
});

    $('a[method="POST"][onclick]').each(function (index, el){
        const onclick = $(el).attr('onclick');

        $(el).removeAttr('onclick');

        $(el).attr('onclick-after', onclick);
    })

