class Task {
  static mapping = { version false }
  static constraints = {
    term(nullable:true)
  }
  private enum Historyfields {
    EXECUTOR, DESCRIPTION, TASKSTATUS, REMAPPER
  }

  Integer id
  Integer tasktype_id
  Integer link = 0
  String description
  Date inputdate = new Date()
  Integer taskstatus
  Date term
  Long initiator
  Long executor
  Long remapper = 0l
  Integer department_id
  Integer company_id = 0

  def transient admin_id = 0

  def afterInsert(){
    new Taskevent(task_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Taskevent(task_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  Boolean isHaveDirty (){ return Task.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

  def csiSetTask(hsInrequest,bNew,lUserId){
    if(bNew){
      taskstatus = 1
      department_id=hsInrequest?.department_id?:0   
      term=Tools.getDate(hsInrequest?.term)
      link=hsInrequest?.link?:0
      initiator=lUserId
      tasktype_id=hsInrequest?.tasktype_id?:0
      company_id=hsInrequest?.company_id?:0
    }else{
      taskstatus=hsInrequest?.taskstatus?:0
    }
    executor=hsInrequest?.executor?:0
    description=hsInrequest?.description?:''

    this
  }

  Task setBaseData(_request){
    department_id = _request.department_id?:0
    executor = _request.executor?:0
    term = Tools.getDate(_request.term)
    link = _request.link?:0
    tasktype_id = _request.tasktype_id?:0
    this
  }

  Task setData(_request){
    description = _request.description?:''
    this
  }

  Task csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Task csiSetTaskstatus(_request){
    taskstatus = _request.taskstatus?:0
    switch (taskstatus){
      case 0: executor = 0
              break
      case 1:
      case 2:
      case 4:
      case 6: break
      case 3: executor = _request.executor?:executor
              remapper = _request.is_remap?admin_id:remapper
              break
      case 5: executor = remapper?:initiator
              remapper = admin_id
              break
    }
    this
  }
}