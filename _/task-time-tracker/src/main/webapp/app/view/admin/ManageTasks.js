Ext.define('TTT.view.admin.ManageTasks', {
    extend: 'Ext.panel.Panel',
    xtype: 'managetasks',
    requires: ['TTT.view.admin.CompanyTree', 'TTT.view.admin.TaskForm', 'TTT.view.admin.ProjectForm', 'TTT.view.admin.CompanyForm', 'Ext.toolbar.Toolbar', 'Ext.layout.container.Card'],
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            dockedItems: [{
                xtype: 'toolbar',
                dock: 'top',
                items: [{
                    xtype: 'button',
                    itemId: 'addCompanyBtn',
                    iconCls: 'addnew',
                    text: 'Add New Company'
                }]
            }],
            items: [{
                xtype: 'companytree',
                flex: 1,
                margin: 1
            }, {
                xtype: 'container',
                itemId: 'adminCards',
                activeItem: 0,
                flex: 1,
                layout: {
                    type: 'card'
                },
                items: [{
                    xtype: 'container',
                    padding: 10,
                    html: 'Please select an item from the tree...'
                }, {
                    xtype: 'companyform'
                }, {
                    xtype: 'projectform'
                }, {
                    xtype: 'taskform'
                }]
            }]
        });
        me.callParent(arguments);
    }
});