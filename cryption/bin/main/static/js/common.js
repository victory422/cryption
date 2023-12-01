/**
 * 
 */

 let maps = [{}];
 
 
 
 	function loadData(type, url, data, callback, errorCallback) {
		$.ajax({
				type: type,
				url : url,
				dataType: "json",
				data : data,
				 headers : {              // Http header      
					 "Content-Type" : "application/json",      
					 "X-HTTP-Method-Override" : "POST"    
				 },
				success: function(results){
					callback(results);
				},
				error: function(data){
					if( typeof errorCallback == 'function' ) {
						errorCallback(data);
					} else {
						console.log(data);
						alert("error가 발생했습니다. validation check");
					}
				}
			});
	}
	
	
	
	function setData(m) {
		var v = document.all;
		for( var i = 0 ;  i < v.length ; i++ ) {
			if( v[i].id != '' && v[i].id != null ) {
				for( var key in m ) {
					if( v[i].id == m["name"]+"."+key ) {
						v[i].innerHTML = m[key];
					}
				}
			}
		}
	}
	
	
	window.addEventListener('load', function() {
            var allElements = document.getElementsByTagName('*');
            Array.prototype.forEach.call(allElements, function(el) {
                var includePath = el.dataset.includePath;
                if (includePath) {
                    var xhttp = new XMLHttpRequest();
                    xhttp.onreadystatechange = function () {
                        if (this.readyState == 4 && this.status == 200) {
                            el.outerHTML = this.responseText;
                        }
                    };
                    xhttp.open('GET', includePath, true);
                    xhttp.send();
                }
            });
        });