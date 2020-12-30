Ext.define('TTT.view.user.ManageUsers', {
    extend: 'Ext.panel.Panel',
    xtype: 'manageusers',

    requires: [
        'Ext.toolbar.Toolbar',
        'TTT.view.user.UserList',
        'TTT.view.user.UserForm'
    ],

    layout: {
        type: 'hbox',
        align:'stretch'
    },

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            itemId:'addUserBtn',
                            iconCls: 'addnew',
                            text: 'Add user'
                        }
                    ]
                }
            ],
            items: [
                {
                    xtype: 'userlist',
                    width:400,
                    margin:1
                },
                {
                    xtype: 'userform',
                    flex: 1
                }
            ]
        });

        me.callParent(arguments);
    }

});