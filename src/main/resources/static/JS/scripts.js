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

const search = () => {
    let query = $("#search-input").val();
    console.log(query);
    if(query == ''){
        $(".search-results").hide();
    }else {
             // sending requests
             let url = `http://localhost:8080/search/${query}`;
             fetch(url)
                 .then(response => response.json())
                 .then(data => {
                     let text = `<div class='list-group'>`;

                     data.forEach(contact => {
                        text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
                     });

                     text += `</div>`;

                     $(".search-results").html(text);
                     $(".search-results").show();
                 })
                 .catch(error => console.log("Error:", error));
         }
};
