
$(document).ready(function () {
    // $("#tbody").empty();
    getBulletin()
})

// Bulletin '게시물'에 대한 function 들
function postBulletin(){
    let title = $("#title-name").val();
    let writing = $("#message-text").val();
    let bulletin = {"title" : title, "writing" : writing}
    if (title==''||writing=='') {
        alert("제목 혹은 글이 비어있습니다.");
        return;
    }
    $.ajax({
        type : "POST",
        url : "/api/bulletins",
        contentType : "application/json",
        data : JSON.stringify(bulletin),
        success : function(response){
            window.location.reload();
        }
    })
}

function getBulletin(){
    $.ajax({
        type:"GET",
        url:"/api/bulletins",
        success: function(response){
            for(let i = 0;i < response.length;i++) {
                let id = response[i]["id"];
                let modifiedAt = response[i]["modifiedAt"].split('T')[0];
                let title = response[i]["title"];
                let writer = response[i]["writer"];
                let tempHtml = `<tr id = "${id}-tr">
                            <td>${writer}</td>
                            <td onclick="getBulletinById(${id})" style="cursor:pointer" data-toggle="modal" data-target="#showModalCenter">${title}</td>
                            <td>${modifiedAt}</td>
                        </tr>`
                $("#tbody").append(tempHtml);
            }
        }
    })
}

function getBulletinById(num){
    $("#showModal").empty();
    $("#postComment").empty();
    $("#updateModal").empty();
    $.ajax({
        type : "GET",
        url : `/api/bulletins/${num}`,
        success : function(response){
            let id = response["id"];
            let modifiedAt = response["modifiedAt"].split('T')[0];
            let title = response["title"];
            let writer = response["writer"];
            let writing = response["writing"];
            let commentList = response["commentList"];
            appendBulletinById(id,modifiedAt,title,writer,writing,commentList);
        }
    })
}

// 조회와 수정 modal 둘 다에 들어간다.
function appendBulletinById(id,modifiedAt,title,writer,writing,commentList){
    let tempHtml_common1 = `<div class="modal-header bulletin-header">
                                <div class="modal-main-header">
                                    <h5 class="modal-title" id="showModalLongTitle">${title}</h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">x</span>
                                    </button>
                                </div>
                                <div class="modal-sub-header" id="showModalWriter">
                                        ${writer},  ${modifiedAt}
                                </div>
                            </div>`
    let tempHtml_user =`<div class="modal-footer bulletin-btns">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="deleteBulletin(${id})">삭제하기</button>
                            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#updatingModal">수정하기</button>
                        </div>`;
    let tempHtml_common2 = `<div class="modal-body" style="white-space:pre-line;">${writing}</div>`
    if(writer == $("#username").text()){
        tempHtml =  tempHtml_common1 + tempHtml_user + tempHtml_common2;
    }else {
        tempHtml =  tempHtml_common1 + tempHtml_common2;
    }
    let tempHtmlComment_btn =`<button type="button" class="btn btn-primary" onclick="postComment(${id})">댓글달기</button>`
    let tempHtml2 = `<div class="modal-header">
                        <div class="modal-main-header">
                            <h5 class="modal-title" id="updatingModalLabel">게시물 작성</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                    </div>
                    <div class="modal-body" id="updatingModalBody">
                        <div class="form-group">
                            <label for="update-recipient-name" class="col-form-label">작성자 : <strong id="update-recipient-name">${writer}</strong> 님</label>
                        </div>
                        <div class="form-group">
                            <label for="update-title-name" class="col-form-label">제목</label>
                            <input type="text" class="form-control" id="update-title-name" value="${title}">
                        </div>
                        <div class="form-group">
                            <label for="update-message-text" class="col-form-label">작성내용</label>
                            <textarea class="form-control" id="update-message-text"">${writing}</textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">닫 기</button>
                        <button type="button" class="btn btn-primary" onclick="updateBulletin(${id})">수정완료</button>
                    </div>`
    $("#showModal").append(tempHtml);
    $("#postComment").append(tempHtmlComment_btn);
    fillCommentList(commentList);
    $("#updateModal").append(tempHtml2);
}

function updateBulletin(num){ // Error 발생 UTF-8
    let title = $("#update-title-name").val();
    let writing = $("#update-message-text").val();
    let bulletin = {"title" : title, "writing" : writing}
    if (title==''||writing=='') {
        alert("수정할 내용을 입력해주세요.");
        return;
    }
    $.ajax({
        type : "PUT",
        url : `/api/bulletins/${num}`,
        contentType : "application/json",
        data : JSON.stringify(bulletin),
        success : function(response){
            window.location.reload();
        }
    });
}

function deleteBulletin(num){
    if (confirm("정말 삭제하시겠습니까?") !== true) {
        return;
    }
    alert("삭제되었습니다.");
    $.ajax({
        type : "DELETE",
        url : `/api/bulletins/${num}`,
        success : function(response){
            window.location.reload();
        }
    })
}

// Comment '게시물'에 대한 function 들
function postComment(bulletinId){
    if ($("#comment-text").val()=='') {
        alert("댓글 내용을 입력해주세요");
        return;
    }else{
        let writing = $("#comment-text").val()
        let comment = {"writing" : writing}
        $.ajax({
            type : "POST",
            url : `/api/bulletins/${bulletinId}/comments`,
            contentType : "application/json",
            data : JSON.stringify(comment),
            success : function(response) {
                alert("댓글 작성이 완료되었습니다.")
                $("#comment-text").val('');
                fillCommentList(response);
            }
        })
    }
}
function fillCommentList(commentList){
    $("#comments").empty();
    for(let i=commentList.length-1 ;i>=0;i--) {
        let co_writer = commentList[i]["writer"];
        let co_writing = commentList[i]["writing"];
        let co_modified = commentList[i]["modifiedAt"].split('T')[0];
        let co_id = commentList[i]["id"];
        let co_tempHtml = `<p class="comment-box" id="comment-show-${co_id}">
                                <strong class="comment-writer">${co_writer} </strong>
                                <span class="comment-update-text" id="comment-finish-text-${co_id}">${co_writing}</span>
                                <span style="color:gray; float: right;">
                                    ${co_modified} 
                                    <button class="update-btn" onclick="deleteComment(${co_id})">삭제</button>
                                    <button class="update-btn" onclick="showUpdate(${co_id})">수정</button>
                                </span>
                            </p>
                            <p class="comment-box" id="comment-update-${co_id}" style="display:none">
                                <strong class="comment-writer">${co_writer} </strong>
                                <textarea class="form-control comment-update-text" id="comment-update-text-${co_id}" style="white-space:pre-line;">${co_writing}</textarea>
                                <span style="color:gray; float: right;">
                                    ${co_modified} 
                                    <button class="update-btn" onclick="hideUpdate(${co_id})">취소</button>
                                    <button class="update-btn" onclick="updateComment(${co_id})">저장</button>
                                </span>
                            </p>`
        $("#comments").append(co_tempHtml);
    }
}

function showUpdate(commentId){
    $.ajax({
        type: "GET",
        url: `/api/checkWriter/${commentId}`,
        success: function (response) {
            console.log(response);
            if(response === true){
                $(`#comment-show-${commentId}`).hide();
                $(`#comment-update-${commentId}`).show();
            } else{
                alert("본인이 작성한 댓글만 수정 가능합니다.");

            }
        }
    })
}
function hideUpdate(commentId){
    $(`#comment-show-${commentId}`).show();
    $(`#comment-update-${commentId}`).hide();
}

function deleteComment(commentId){
    $.ajax({
        type: "GET",
        url: `/api/checkWriter/${commentId}`,
        success: function (response) {
            if(response === true){
                if (confirm("정말 삭제하시겠습니까?") !== true) {
                    return;
                }
                alert("삭제되었습니다.");
                $.ajax({
                    type: "DELETE",
                    url: `/api/comments/${commentId}`,
                    success: function (response) {
                        $(`#comment-update-${response}`).remove();
                        $(`#comment-show-${response}`).remove();
                    }
                })
            }else{
                alert("본인이 작성한 댓글만 삭제 가능합니다.")
            }
        }
    });
}

function updateComment(commentId){
    let writing = $(`#comment-update-text-${commentId}`).val();
    if (writing == '') {
        alert("수정할 내용을 입력해주세요");
        return;
    }
    $.ajax({
        type: "PUT",
        url: `/api/comments/${commentId}`,
        contentType: "application/json",
        data: JSON.stringify({"writing": writing}),
        success: function (response) {
            alert("수정이 완료 되었습니다.")
            $(`#comment-finish-text-${response}`).text(writing);
            hideUpdate(response);
        }
    })
}



