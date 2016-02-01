
$(function(){
    var r = new Resumable({
        target:'/resumable'
    });

    r.assignBrowse(document.getElementById('browseButton'));
    //r.on('fileAdded', function(file){
    //    r.upload();
    //});
    $("#button").click( function()
        {
            r.upload();
        }
    );
    r.on('fileSuccess', function(file){
        console.debug(file);
    });
    r.on('fileProgress', function(file){
        console.debug(file);
    });
// more events, look API docs
});