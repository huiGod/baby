
//点击选择答案
function clickChecked() {
    $('.question .q-option').click(function () {

        $(this).siblings('.q-option').removeClass('active');
        $(this).addClass('active');

        if($(this).attr('if') == "true"){
            $(this).siblings('.btn-right').removeClass('error');

        }else{
            $(this).siblings('.btn-right').addClass('error');
        }
        $(this).siblings('.btn-right').show();

    })
}

//点击提交答案
function clickSub() {

    $('.footer .f-sub').click(function () {
        var aQuestion = $('.question .btn-right');
        var answer = 0;
        for(var i= 0 ;i<aQuestion.length;i++){
            console.log(aQuestion[i].style.display);
            if(aQuestion[i].style.display==''){
                answer = 1;
            }
        }
        if(answer == 1){
            $('.popup .p-text').hide();
            $('.popup .p-text').eq(0).show()
        }else{
            if($('.question .error').length>0){

                $('.popup .p-text').hide();
                $('.popup .p-text').eq(1).show()
            }else if($('.question .error').length==0){

                $('.popup .p-text').hide();
                $('.popup .p-text').eq(2).show()
            }
        }
        $('.popup').show();
    });
    $('.popup .p-text .p-btn').click(function () {
        $('.popup').hide();
    });
}

$(document).ready(function () {

    clickChecked();                 //点击选择答案
    clickSub();                     //点击提交答案


});