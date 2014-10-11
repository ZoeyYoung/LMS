var language = {
    "search": "搜索:",
    "sSearchPlaceholder": "关键字",
    "infoEmpty": "暂无记录",
    "emptyTable": "暂无记录",
    "info": "_PAGE_ / _PAGES_",
    "paginate": {
        "first": "<<",
        "last": ">>",
        "next": "下一页",
        "previous": "上一页"
    },
    "infoFiltered": "",
    "zeroRecords": "暂无记录",
    "lengthMenu": "每页 _MENU_ 条"
};

$(function() {
    $('#side-menu').metisMenu();
    $('#dataTables').dataTable({
        "language": language
    });
});
//Loads the correct sidebar on window load,
//collapses the sidebar on window resize.
// Sets the min-height of #page-wrapper to window size
$(function() {
    $(window).bind("load resize", function() {
        topOffset = 50;
        width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
        if (width < 768) {
            $('div.navbar-collapse').addClass('collapse')
            topOffset = 100; // 2-row-menu
        } else {
            $('div.navbar-collapse').removeClass('collapse')
        }
        height = (this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height;
        height = height - topOffset;
        if (height < 1) height = 1;
        if (height > topOffset) {
            $("#page-wrapper").css("min-height", (height) + "px");
        }
    })
})
