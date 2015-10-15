modules = {
    application {
      resource url:'js/application.js'
    }
    'prototype/prototype' {    
      dependsOn 'application'
        resource url:'js/prototype/prototype.js', disposition: 'head'
    }
    'prototype/autocomplete' {
        resource url:'js/prototype/autocomplete.js', disposition: 'head'
    }
    'jquery-1.10.1.min' {
      resource url:'js/jquery-1.10.1.min.js', disposition: 'head'
    }
    'html5' {
       resource url:'js/html5.js', disposition: 'head'
    }
    'kendo.culture.ru-RU.min' {
      dependsOn 'jquery-1.10.1.min'
      dependsOn 'kendo.web.min'
      resource url:'js/kendo.culture.ru-RU.min.js', disposition: 'head'
    }
    'kendo.web.min' {
      dependsOn 'jquery-1.10.1.min'
      resource url:'js/kendo.web.min.js', disposition: 'head'
    }
    'jquery.maskedinput.min' {
      dependsOn 'jquery-1.10.1.min'
      resource url:'js/jquery.maskedinput.min.js', disposition: 'head'
    }
    'superfish.min' {
      dependsOn 'jquery-1.10.1.min'
      resource url:'js/superfish.min.js', disposition: 'head'
    }
}