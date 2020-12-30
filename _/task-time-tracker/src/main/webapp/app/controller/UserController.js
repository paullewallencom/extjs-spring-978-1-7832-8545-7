Ext.define('TTT.controller.UserController', {
    extend: 'Ext.app.Controller',
    views: ['user.ManageUsers'],
    refs: [{
        ref: 'userList',
        selector: 'manageusers userlist'
    }, {
        ref: 'userForm',
        selector: 'manageusers userform'
    }, {
        ref: 'addUserButton',
        selector: 'manageusers #addUserBtn'
    }, {
        ref: 'saveUserButton',
        selector: 'manageusers userform #saveBtn'
    }, {
        ref: 'deleteUserButton',
        selector: 'manageusers userform #deleteBtn'
    }, {
        ref: 'userFormFieldset',
        selector: 'manageusers userform fieldset'
    }, {
        ref: 'usernameField',
        selector: 'manageusers userform textfield[name=username]'
    }],
    init: function(application) {
        this.control({
            'manageusers #addUserBtn': {
                click: this.doAddUser
            },
            'userlist': {
                itemclick: this.doSelectUser,
                viewready: this.doInitStore
            },
            'manageusers userform #saveBtn': {
                click: this.doSaveUser
            },
            'manageusers userform #deleteBtn': {
                click: this.doDeleteUser
            },
            'manageusers userform': {
                afterrender: this.doAddUser
            },
            'userlist header tool[type="refresh"]': {
                click: this.doRefreshUserList
            }
        });
    },
    doInitStore: function() {
        this.getUserList().getStore().load();
    },
    doAddUser: function() {
        var me = this;
        me.getUserFormFieldset().setTitle('Add New User');
        me.getUsernameField().enable();
        var newUserRec = Ext.create('TTT.model.User', {
            adminRole: 'N'
        });
        me.getUserForm().loadRecord(newUserRec);
        me.getDeleteUserButton().disable();
    },
    doSelectUser: function(grid, record) {
        var me = this;
        me.getUserForm().loadRecord(record);
        me.getUserFormFieldset().setTitle('Edit User ' + record.get('username'));
        me.getUsernameField().disable();
        me.getDeleteUserButton().enable();
    },
    doSaveUser: function() {
        var me = this;
        var rec = me.getUserForm().getRecord();
        if (rec !== null) {
            me.getUserForm().updateRecord();
            var errs = rec.validate();
            if (errs.isValid()) {
                rec.save({
                    success: function(record, operation) {
                        if (typeof record.store === 'undefined') {
                            // the record is not yet in a store 
                            me.getUserList().getStore().add(record);
                            // select the user in the grid
                            me.getUserList().getSelectionModel().select(record,true);
                        }
                        me.getUserFormFieldset().setTitle('Edit User ' + record.get('username'));
                        me.getUsernameField().disable();
                        me.getDeleteUserButton().enable();
                    },
                    failure: function(rec, operation) {
                        Ext.Msg.alert('Save Failure', operation.request.scope.reader.jsonData.msg);
                    }
                });
            } else {
                me.getUserForm().getForm().markInvalid(errs);
                Ext.Msg.alert('Invalid Fields', 'Please fix the invalid entries!');
            }
        }
    },
    doDeleteUser: function() {
        var me = this;
        var rec = me.getUserForm().getRecord();
        Ext.Msg.confirm('Confirm Delete User', 'Are you sure you want to delete user ' + rec.get('fullName') + '?', function(btn) {
            if (btn === 'yes') {
                rec.destroy({
                    failure: function(rec, operation) {
                        Ext.Msg.alert('Delete Failure', operation.request.scope.reader.jsonData.msg);
                    }
                });
                me.doAddUser();
            }
        });
    },
    doRefreshUserList: function() {
        this.getUserList().getStore().load();
    }
});