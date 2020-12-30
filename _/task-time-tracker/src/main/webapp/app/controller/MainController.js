Ext.define('TTT.controller.MainController', {
    extend: 'Ext.app.Controller',
    requires:[
        'Ext.window.MessageBox'
    ],
    views: [
        'TTT.view.MainHeader',
        'TTT.view.MainCards',
        'TTT.view.LogonWindow'
    ],
    refs: [        
        {
            ref: 'mainCards',
            selector: 'maincards'
        },        
        {
            ref:'usernameField',
            selector:'logonwindow textfield[name=username]'
        },
        {
            ref:'passwordField',
            selector:'logonwindow textfield[name=password]'
        }
    ],
    init: function(application) {
        
        this.control({
            'mainheader button': {
                click: this.doHeaderButtonClick
            },
            'logonwindow button': {
                click: this.doLogon
            }
        });
    },
            
    doHeaderButtonClick : function(button, e, options) {

        var me = this;
        
        if(button.itemId === 'userAdminBtn'){
            
            me.getMainCards().getLayout().setActiveItem('manageUsersCard');
            
        } else if(button.itemId === 'taskAdminBtn'){
            
            me.getMainCards().getLayout().setActiveItem('manageTasksCard');
            
        } else if(button.itemId === 'taskLogsBtn'){
            
            me.getMainCards().getLayout().setActiveItem('taskLogCard');
            
        } else if(button.itemId === 'logoffBtn'){
            
            me.doLogoff();
            
        }
    },

    doLogon: function() {
        
        var me = this;
        
        if(me.getUsernameField().validate() && me.getPasswordField().validate() ){
            
            Ext.Ajax.request({
                url: 'security/logon.json',
                params: {
                    username: me.getUsernameField().getValue(),
                    password: me.getPasswordField().getValue()
                },
                success: function(response) {

                    var obj = Ext.JSON.decode(response.responseText);
                    
                    if(obj.success){
                        
                        //TTT.console(obj);
                        TTT.getApplication().doAfterLogon(obj.data);
                        
                    } else {                        
                        Ext.Msg.alert('Invalid Logon','Please enter a valid username and password');                        
                    }
                }
            });
            
        } else {
            Ext.Msg.alert('Invalid Logon','Please enter a valid username and password');
        }        
    },
    doLogoff : function(){
        
        Ext.Msg.confirm('Confirm Logout', 'Are you sure you want to log out of 3T?', function(button){
                
                if(button === 'yes'){
                    Ext.Ajax.request({
                       url: 'security/logout.json',
                       success: function(){
                           window.location.reload();
                       }
                    });
                }
            }
        );        
    }
});
