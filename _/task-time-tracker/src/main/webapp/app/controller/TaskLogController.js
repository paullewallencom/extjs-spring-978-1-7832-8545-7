Ext.define('TTT.controller.TaskLogController', {
    extend: 'Ext.app.Controller',
    views: ['tasklog.ManageTaskLogs'],
    stores: ['TaskLog', 'Project', 'Task'],
    refs: [{
        ref: 'taskLogList',
        selector: 'managetasklogs taskloglist'
    }, {
        ref: 'taskLogForm',
        selector: 'managetasklogs tasklogform'
    }, {
        ref: 'addTaskLogButton',
        selector: 'managetasklogs #addTaskLogBtn'
    }, {
        ref: 'saveTaskLogButton',
        selector: 'managetasklogs tasklogform #saveBtn'
    }, {
        ref: 'searchTaskLogsButton',
        selector: 'managetasklogs #searchBtn'
    }, {
        ref: 'deleteTaskLogButton',
        selector: 'managetasklogs tasklogform #deleteBtn'
    }, {
        ref: 'taskLogFormFieldset',
        selector: 'managetasklogs tasklogform fieldset'
    }, {
        ref: 'startDateField',
        selector: 'managetasklogs datefield[name=startDate]'
    }, {
        ref: 'endDateField',
        selector: 'managetasklogs datefield[name=endDate]'
    }, {
        ref: 'taskCombo',
        selector: 'managetasklogs tasklogform combo[name=idTask]'
    }, {
        ref: 'projectCombo',
        selector: 'managetasklogs tasklogform combo[name=project]'
    }, {
        ref: 'taskLogDateField',
        selector: 'managetasklogs tasklogform datefield[name=taskLogDate]'
    }, {
        ref: 'taskHoursField',
        selector: 'managetasklogs tasklogform #taskHours'
    }],
    init: function(application) {
        this.control({
            'managetasklogs #addTaskLogBtn': {
                click: this.doAddTaskLog
            },
            'managetasklogs taskloglist': {
                itemclick: this.doSelectTaskLog
            },
            'managetasklogs tasklogform #saveBtn': {
                click: this.doSaveTaskLog
            },
            'managetasklogs': {
                activate: this.doAfterActivate
            },
            'managetasklogs tasklogform #deleteBtn': {
                click: this.doDeleteTaskLog
            },
            'managetasklogs #searchBtn': {
                click: this.doSearch
            },
            'managetasklogs tasklogform combobox[name=project]': {
                select: this.doSelectProject
            }
        });
    },
    doAfterActivate: function() {
        var me = this;
        me.getTaskStore().load();
        me.getProjectStore().load();
    },           
    doSelectProject: function(combo, records) {
        var me = this;
        var rec = records[0];
        if (!Ext.isEmpty(rec)) {
            me.getTaskCombo().getStore().clearFilter();
            me.getTaskCombo().getStore().filter({
                property: 'idProject',
                value: rec.get('idProject'),
                exactMatch: true
            });
            me.getTaskCombo().setValue('');
            if (me.getTaskCombo().getStore().getCount() === 0) {
                Ext.Msg.alert('No Tasks Available', 'There are no tasks assigned to this project!');
            }
        }
    },
    doSelectTaskLog: function(grid, record) {
        var me = this;
        me.getTaskCombo().getStore().clearFilter();
        me.getTaskCombo().getStore().filter({
            property: 'idProject',
            value: record.get('idProject'),
            exactMatch: true
        });
        me.getProjectCombo().setValue(record.get('idProject'));
        me.getTaskLogForm().loadRecord(record);
        me.getTaskLogFormFieldset().show();
        me.getTaskLogFormFieldset().setTitle('Edit Task Log For ' + record.get('taskName'));
        me.getTaskLogForm().getForm().clearInvalid();
        me.getDeleteTaskLogButton().enable();
    },
    doAddTaskLog: function() {
        var me = this;
        me.getTaskLogFormFieldset().show();
        me.getTaskLogFormFieldset().setTitle('Add Task Log');
        var taskLogDate = me.getTaskLogDateField().getValue();
        if (Ext.isEmpty(taskLogDate)) {
            taskLogDate = new Date();
        }
        var tl = Ext.create('TTT.model.TaskLog', {
            taskDescription: '',
            username: TTT.getApplication().getUser().username,
            taskLogDate: taskLogDate,
            taskMinutes: 0,
            idTask: null
        });
        me.getTaskLogForm().loadRecord(tl);
        me.getDeleteTaskLogButton().disable();
        var idProject = me.getProjectCombo().getValue();
        if (Ext.isEmpty(idProject)) {
            var firstRec = me.getProjectCombo().getStore().getAt(0);
            me.getProjectCombo().setValue(firstRec.get('idProject'), true);
            me.getTaskCombo().getStore().clearFilter();
            me.getTaskCombo().getStore().filter({
                property: 'idProject',
                value: firstRec.get('idProject'),
                exactMatch: true
            });
            me.getTaskCombo().setValue('');
        }
    },
    doDeleteTaskLog: function() {
        var me = this;
        var rec = me.getTaskLogForm().getRecord();
        Ext.Msg.confirm('Confirm Delete', 'Are you sure you want to delete this task log?', function(btn) {
            if (btn === 'yes') {
                rec.destroy({
                    failure: function(rec, operation) {
                        Ext.Msg.alert('Delete Failure', operation.request.scope.reader.jsonData.msg);
                    }
                });
                me.doAddTaskLog();
            }
        });
    },
    doSaveTaskLog: function() {
        var me = this;
        var rec = me.getTaskLogForm().getRecord();
        if (!Ext.isEmpty(rec)) {
            me.getTaskLogForm().updateRecord(); 
            // update the minutes field of the record
            var hours = me.getTaskHoursField().getValue();
            rec.set('taskMinutes', hours * 60);
            var errs = rec.validate();
            if (errs.isValid() && me.getTaskLogForm().isValid()) {
                rec.save({
                    success: function(record, operation) {
                        if (typeof record.store === 'undefined') {
                            me.getTaskLogStore().add(record);
                        }
                        me.getTaskLogFormFieldset().setTitle('Edit Task Log For ' + record.get('taskName'));
                        me.getDeleteTaskLogButton().enable();
                    },
                    failure: function(rec, operation) {
                        Ext.Msg.alert('Save Failure', operation.request.scope.reader.jsonData.msg);
                    }
                });
            } else {
                me.getTaskLogForm().getForm().markInvalid(errs);
                Ext.Msg.alert('Invalid Fields', 'Please fix the invalid entries!');
            }
        }
    },
    doSearch: function() {
        var me = this;
        var startDate = me.getStartDateField().getValue();
        if (Ext.isEmpty(startDate)) {
            Ext.Msg.alert('Start Date Required', 'Please select a valid start date to perform a search');
            return;
        }
        var endDate = me.getEndDateField().getValue();
        if (Ext.isEmpty(endDate)) {
            Ext.Msg.alert('End Date Required', 'Please select a valid end date to perform a search');
            return;
        }
        me.getTaskLogStore().doFindByUser(TTT.getApplication().getUser().username, startDate, endDate);
        me.getTaskLogFormFieldset().hide();
    }
});