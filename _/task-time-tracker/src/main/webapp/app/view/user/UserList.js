Ext.define('TTT.view.user.UserList', {
    extend: 'Ext.grid.Panel',
    xtype: 'userlist',
    store: 'User',
    title: 'User List',
    viewConfig: {
        markDirty: false,
        stripeRows: false
    },
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            tools: [{
                type: 'refresh',
                tooltip: 'Refresh user list'
            }],
            columns: [{
                xtype: 'gridcolumn',
                dataIndex: 'username',
                flex: 1,
                text: 'Username'
            }, {
                xtype: 'gridcolumn',
                dataIndex: 'firstName',
                flex: 1,
                text: 'First Name'
            }, {
                xtype: 'gridcolumn',
                flex: 1,
                dataIndex: 'lastName',
                text: 'Last Name'
            }, {
                xtype: 'gridcolumn',
                flex: 2,
                dataIndex: 'email',
                text: 'Email'
            }]
        });
        me.callParent(arguments);
    }
});