Ext.define('TTT.view.user.UserForm', {
    extend: 'Ext.form.Panel',
    xtype: 'userform',
    requires: ['Ext.form.FieldSet', 'Ext.form.field.Radio', 'Ext.form.RadioGroup', 'Ext.toolbar.Toolbar'],
    layout: {
        type: 'anchor'
    },
    bodyPadding: 10,
    border: false,
    autoScroll: true,
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            items: [{
                xtype: 'fieldset',
                padding: 10,
                width: 350,
                fieldDefaults: {
                    anchor: '100%'
                },
                title: 'User',
                items: [{
                    xtype: 'textfield',
                    name: 'username',
                    fieldLabel: 'Username'
                }, {
                    xtype: 'textfield',
                    name: 'firstName',
                    fieldLabel: 'First Name'
                }, {
                    xtype: 'textfield',
                    name: 'lastName',
                    fieldLabel: 'Last Name'
                }, {
                    xtype: 'textfield',
                    name: 'email',
                    fieldLabel: 'Email'
                }, {
                    xtype: 'textfield',
                    name: 'password',
                    inputType: 'password',
                    fieldLabel: 'Password'
                }, {
                    xtype: 'radiogroup',
                    fieldLabel: 'Administrator',
                    items: [{
                        boxLabel: 'Yes',
                        name: 'adminRole',
                        inputValue: 'Y'
                    }, {
                        boxLabel: 'No',
                        name: 'adminRole',
                        inputValue: 'N'
                    }]
                }, {
                    xtype: 'toolbar',
                    ui: 'footer',
                    layout: {
                        pack: 'end',
                        type: 'hbox'
                    },
                    items: [{
                        xtype: 'button',
                        itemId: 'deleteBtn',
                        iconCls: 'delete',
                        text: 'Delete'
                    }, {
                        xtype: 'button',
                        itemId: 'saveBtn',
                        iconCls: 'save',
                        text: 'Save'
                    }]
                }]
            }]
        });
        me.callParent(arguments);
    }
});