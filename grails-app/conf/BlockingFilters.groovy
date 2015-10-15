class BlockingFilters {

    def filters = {
        systemblock(controller: 'user', action: 'index|login|logout|checkUser', invert: true) {
            before = {
                if (session.user?.accesslevel == 0 && Tools.getIntVal(Dynconfig.findByName('system.activity.status')?.value,0) != 1){
                    redirect(controller: 'user', action: 'logout')
                    return false
                }
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
