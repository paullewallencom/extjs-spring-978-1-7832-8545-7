Ext.define('TTT.controller.AdminController', {
    extend: 'Ext.app.Controller',
    requires:['Ext.Number'],
    stores: ['Company', 'Project', 'Task', 'CompanyTree'],
    views: ['admin.ManageTasks'],
    refs: [{
        ref: 'companyTree',
        selector: 'managetasks companytree'
    },{
        ref: 'adminCards',
        selector: 'managetasks #adminCards'
    }, {
        ref: 'companyForm',
        selector: 'managetasks companyform'
    }, {
        ref: 'projectForm',
        selector: 'managetasks projectform'
    }, {
        ref: 'taskForm',
        selector: 'managetasks taskform'
    }, {
        ref: 'addCompanyButton',
        selector: 'managetasks #addCompanyBtn'
    }, {
        ref: 'addProjectButton',
        selector: 'managetasks companyform #addProjectBtn'
    }, {
        ref: 'deleteCompanyButton',
        selector: 'managetasks companyform #deleteBtn'
    }, {
        ref: 'deleteProjectButton',
        selector: 'managetasks projectform #deleteBtn'
    }, {
        ref: 'addTaskButton',
        selector: 'managetasks projectform #addTaskBtn'
    }, {
        ref: 'deleteTaskButton',
        selector: 'managetasks taskform #deleteBtn'
    }, {
        ref: 'companyFormFieldset',
        selector: 'managetasks companyform fieldset'
    }, {
        ref: 'projectFormFieldset',
        selector: 'managetasks projectform fieldset'
    }, {
        ref: 'taskFormFieldset',
        selector: 'managetasks taskform fieldset'
    }, {
        ref: 'usernameField',
        selector: 'managetasks userform textfield[name=username]'
    }],
    init: function() {
        this.control({  
            'managetasks': {
                activate: this.doAfterActivate
            },
            'managetasks #addCompanyBtn': {
                click: this.doAddCompany
            },
            'managetasks companytree': {
                itemclick: this.doSelectTreeItem
            },
            'managetasks companyform #saveBtn': {
                click: this.doSaveCompany
            },
            'managetasks companyform #deleteBtn': {
                click: this.doDeleteCompany
            },
            'managetasks companyform #addProjectBtn': {
                click: this.doAddProject
            },
            'managetasks projectform #saveBtn': {
                click: this.doSaveProject
            },
            'managetasks projectform #deleteBtn': {
                click: this.doDeleteProject
            },
            'managetasks projectform #addTaskBtn': {
                click: this.doAddTask
            },
            'managetasks taskform #saveBtn': {
                click: this.doSaveTask
            },
            'managetasks taskform #deleteBtn': {
                click: this.doDeleteTask
            },
            'managetasks companyform': {
                afterrender: this.doAddCompany
            },
            'managetasks companytree header tool[type="refresh"]': {
                click: this.doRefreshTree
            },
            'managetasks companytree header tool[type="collapse"]': {
                click: this.doCollapseTree
            },
            'managetasks companytree header tool[type="expand"]': {
                click: this.doExpandTree
            }
        });
    },
    doAfterActivate:function(){

        var me = this;
        me.getCompanyStore().load();
        me.getProjectStore().load();
        me.getTaskStore().load();
        me.getCompanyTreeStore().on('append' , me.doSetTreeIcon, me);
        me.getCompanyTree().getView().on('beforedrop' , me.isDropAllowed, me);
        me.getCompanyTree().getView().on('drop', me.doChangeParent, me);        
        me.getCompanyTreeStore().load();
        
    },
    doSetTreeIcon: function(store, node, refNode, eOpts) {
        var nodeType = node.getId().substring(0, 1);
        if (nodeType === 'C') {
            node.set('iconCls', 'company');
        } else if (nodeType === 'P') {
            node.set('iconCls', 'project');
        } else if (nodeType === 'T') {
            node.set('iconCls', 'task');
        }
    },            
    doSelectTreeItem: function(tree, record) {
        var me = this;
        var recIdSplit = record.getId().split('_');
        TTT.console(recIdSplit);
        if (recIdSplit[0]==='T') {
            var idTask = Ext.Number.from(recIdSplit[1]);
            var rec = me.getTaskStore().getById(idTask);
            if (!Ext.isEmpty(rec)) {
                me.getTaskForm().loadRecord(rec);
                me.getTaskFormFieldset().setTitle('Edit Task for ' + rec.get('projectName'));
                me.getDeleteTaskButton().enable();
                me.getAdminCards().getLayout().setActiveItem(me.getTaskForm());
            }
        } else if (recIdSplit[0]==='P') {
            var idProject = Ext.Number.from(recIdSplit[1]);
            var rec = me.getProjectStore().getById(idProject);
            if (!Ext.isEmpty(rec)) {
                me.getProjectForm().loadRecord(rec);
                me.getProjectFormFieldset().setTitle('Edit Project for ' + rec.get('companyName'));
                me.getDeleteProjectButton().enable();
                me.getAddTaskButton().enable();
                me.getAdminCards().getLayout().setActiveItem(me.getProjectForm());
            }
        } else if (recIdSplit[0]==='C') {
            var idCompany = Ext.Number.from(recIdSplit[1]);
            var rec = me.getCompanyStore().getById(idCompany);
            if (!Ext.isEmpty(rec)) {
                me.getCompanyForm().loadRecord(rec);
                me.getCompanyFormFieldset().setTitle('Edit Company');
                me.getDeleteCompanyButton().enable();
                me.getAddProjectButton().enable();
                me.getAdminCards().getLayout().setActiveItem(me.getCompanyForm());
            }
        } else { 
            TTT.console('Invalid record selected?');
        }
    },
    doAddCompany: function() {
        var me = this;
        var newRec = Ext.create('TTT.model.Company');
        me.getCompanyForm().loadRecord(newRec);
        me.getCompanyFormFieldset().setTitle('Add Company');
        me.getDeleteCompanyButton().disable();
        me.getAddProjectButton().disable();
        me.getAdminCards().getLayout().setActiveItem(me.getCompanyForm());
        me.doDeselectAll();
    },
    doSaveCompany: function() {
        var me = this;
        var rec = me.getCompanyForm().getRecord();
        if (rec !== null) {
            me.getCompanyForm().updateRecord();
            var errs = rec.validate();
            if (errs.isValid()) {
                rec.save({
                    success: function(record, operation) {
                        me.getCompanyStore().load();
                        me.getCompanyFormFieldset().setTitle('Edit Company');
                        me.getDeleteCompanyButton().enable();
                        me.getAddProjectButton().enable();
                        me.doRefreshTree();
                    },
                    failure: function(rec, operation) {
                        Ext.Msg.alert('Save Failure', operation.request.scope.reader.jsonData.msg);
                    }
                });
            } else {
                me.getCompanyForm().getForm().markInvalid(errs);
                Ext.Msg.alert('Invalid Fields', 'Please fix the invalid entries!');
            }
        }
    },
    doDeleteCompany: function() {
        var me = this;
        var rec = me.getCompanyForm().getRecord();
        Ext.Msg.confirm('Confirm Delete', 'Are you sure you want to delete this company?', function(btn) {
            if (btn === 'yes') {
                rec.destroy({
                    success: function() {
                        me.getCompanyStore().load();
                        me.getAdminCards().getLayout().setActiveItem(0);
                        me.doRefreshTree(); 
                    },
                    failure: function(rec, operation) { 
                        Ext.Msg.alert('Delete Failure', operation.request.scope.reader.jsonData.msg);
                    }
                }); 
            }
        });
    },
    doAddProject: function() {
        var me = this;
        var company = me.getCompanyForm().getRecord();
        var newRec = Ext.create('TTT.model.Project', {
            'idCompany': company.get('idCompany')
        });
        me.getProjectForm().loadRecord(newRec);
        me.getProjectFormFieldset().setTitle('Add Project to ' + company.get('companyName'));
        me.getDeleteProjectButton().disable();
        me.getAddTaskButton().disable();
        me.getAdminCards().getLayout().setActiveItem(me.getProjectForm());
    },
    doSaveProject: function() {
        var me = this;
        var rec = me.getProjectForm().getRecord();
        TTT.console(rec);
        if (rec !== null) {
            me.getProjectForm().updateRecord();
            var errs = rec.validate();
            if (errs.isValid()) {
                rec.save({
                    success: function(record) {
                        me.getProjectFormFieldset().setTitle('Edit Project for ' + record.get('companyName'));
                        me.getDeleteProjectButton().enable();
                        me.getAddTaskButton().enable();
                        me.getProjectStore().load();
                        me.doRefreshTree();
                    },
                    failure: function(rec, operation) {
                        Ext.Msg.alert('Save Failure', operation.request.scope.reader.jsonData.msg);
                    }
                });
            } else {
                me.getProjectForm().getForm().markInvalid(errs);
                Ext.Msg.alert('Invalid Fields', 'Please fix the invalid entries!');
            }
        }
    },
    doDeleteProject: function() {
        var me = this;
        var rec = me.getProjectForm().getRecord();
        Ext.Msg.confirm('Confirm Delete', 'Are you sure you want to delete this project?', function(btn) {
            if (btn === 'yes') {
                rec.destroy({
                    success: function() {
                        me.getProjectStore().load();
                        me.getAdminCards().getLayout().setActiveItem(0);
                        me.doRefreshTree();
                    },
                    failure: function(rec, operation) {
                        Ext.Msg.alert('Delete Failure', operation.request.scope.reader.jsonData.msg);
                    }
                });
            }
        });
    },
    doAddTask: function() {
        var me = this;
        var project = me.getProjectForm().getRecord();
        var newRec = Ext.create('TTT.model.Task', {
            'idProject': project.get('idProject')
        });
        me.getTaskForm().loadRecord(newRec);
        me.getTaskFormFieldset().setTitle('Add Task to ' + project.get('projectName'));
        me.getDeleteTaskButton().disable();
        me.getAdminCards().getLayout().setActiveItem(me.getTaskForm());
    },
    doSaveTask: function() {
        var me = this;
        var rec = me.getTaskForm().getRecord();
        if (rec !== null) {
            me.getTaskForm().updateRecord();
            var errs = rec.validate();
            if (errs.isValid()) {
                rec.save({
                    success: function(record, operation) {
                        me.getTaskFormFieldset().setTitle('Edit Task for ' + record.get('projectName'));
                        me.getDeleteTaskButton().enable();
                        me.getTaskStore().load();
                        me.doRefreshTree();
                    },
                    failure: function(rec, operation) {
                        Ext.Msg.alert('Save Failure', operation.request.scope.reader.jsonData.msg);
                    }
                });
            } else {
                me.getTaskForm().getForm().markInvalid(errs);
                Ext.Msg.alert('Invalid Fields', 'Please fix the invalid entries!');
            }
        }
    },
    doDeleteTask: function() {
        var me = this;
        var rec = me.getTaskForm().getRecord();
        Ext.Msg.confirm('Confirm Delete', 'Are you sure you want to delete this task?', function(btn) {
            if (btn === 'yes') {
                rec.destroy({
                    success: function() {
                        me.getTaskStore().load();
                        me.getAdminCards().getLayout().setActiveItem(0);
                        me.doRefreshTree();
                    },
                    failure: function(rec, operation) {
                        Ext.Msg.alert('Delete Failure', operation.request.scope.reader.jsonData.msg);
                    }
                });
            }
        });
    },
    doRefreshTree: function() {
        var me = this;
        me.getCompanyTreeStore().load();
    },
    doExpandTree: function() {
        this.getCompanyTree().expandAll();
    },
    doCollapseTree: function() {
        this.getCompanyTree().collapseAll();
    },
    doDeselectAll: function() {
        var me = this;
        var recs = me.getCompanyTree().getView().getSelectionModel().getSelection();
        me.getCompanyTree().getView().deselect(recs, false);
    },
    isDropAllowed : function(node, data, overModel, dropPosition){
        
        var dragNode = data.records[0];
        
        if(!Ext.isEmpty(dragNode) && !Ext.isEmpty(overModel)){
            
            var dragIdSplit = dragNode.getId().split('_');
            var dropIdSplit = overModel.getId().split('_');
            
            if(dragIdSplit[0] === 'T' && dropIdSplit[0] === 'P'){
                return true;
            } else if(dragIdSplit[0] === 'P' && dropIdSplit[0] === 'C'){
                return true;
            } 
        }
        
        TTT.console('drop not allowed...');
        return false;
        
    },
    doChangeParent : function(node, data, overModel, dropPosition, eOpts){
        
        var me = this;
        var dragNode = data.records[0];
        if(!Ext.isEmpty(dragNode) && !Ext.isEmpty(overModel)){
            
            var dragIdSplit = dragNode.getId().split('_');
            var dropIdSplit = overModel.getId().split('_');
            
            if(dragIdSplit[0] === 'T' && dropIdSplit[0] === 'P'){
                
                var idTask = Ext.Number.from(dragIdSplit[1]);
                var idProject = Ext.Number.from(dropIdSplit[1]);
                var rec = me.getTaskStore().getById(idTask);
                
                if (!Ext.isEmpty(rec)) {
                    rec.set('idProject', idProject);
                    rec.save();
                }
                                
            } else if(dragIdSplit[0] === 'P' && dropIdSplit[0] === 'C'){
                
                var idProject = Ext.Number.from(dragIdSplit[1]);
                var idCompany = Ext.Number.from(dropIdSplit[1]);
                
                var rec = me.getProjectStore().getById(idProject);
                
                if (!Ext.isEmpty(rec)) {
                    rec.set('idCompany', idCompany);
                    rec.save();
                }                
            } 
        }        
    }
});