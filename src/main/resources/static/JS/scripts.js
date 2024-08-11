const toggleSidebar = () =>{
    if($('.sidebar').is(":visible")){
        //true and close the sidebar
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left","0%");
    }else{
        //false and open the sidebar
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left","20%");
    }
}