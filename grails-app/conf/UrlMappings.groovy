class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller : "user", action:"index")

		"/report/$group"{
			controller = "report"
			action = "index"
			group_id = { Reportgroup.findByAction(params.group)?.id?:-1 }
			constraints {
				group(validator:{return Reportgroup.findByAction(it)||it=='error'?true:false})
			}
		}

		"/cash/scan/$id/$code"{
			controller = "cash"
			action = "showscan"
			constraints {
				id(matches:/\d+/)
			}
		}
    "/favicon.ico"(uri:"/favicon.ico")		

		"500"(view:'/error')
		"401"(view:'/error_401')
		"403"(view:'/error_403')
	}
}
