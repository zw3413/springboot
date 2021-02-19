

var app = new Vue({
    el: '#app',
    vuetify: new Vuetify(),
    components:{
        'KMZ': KMZ
    },
    data: function () {
        return {

            viewer: viewer,

            options:options,
            file_selected: [],
            headers: [
                {
                    text: '文件名称',
                    align: 'start',
                    sortable: false,
                    value: 'filename',
                },
                {text: '文件类型', value: 'filetype'},
                {text: '文件路径', value: 'filepath'},
                {text: '上传时间', value: 'createtime'}
            ],
            files: [],
            file: null,



            items: [
            ],
            item_current:[],
            item_selected:[],
            ds:{},
            dialog: false,
            widgets: false,
            title: '时空数据系统'

        }
    },
    methods: {
        addKML:function(id){
            //options.offset = new Cesium.HeadingPitchRange(Cesium.Math.toRadians(0), Cesium.Math.toRadians(45), 2500);
            var promise = Cesium.KmlDataSource.load('./data/kml/'+id, options);
            this.viewer.dataSources.add(promise)
            this.viewer.flyTo(promise,options);

            var that=this;
            Cesium.when(promise, function(dataSource){
                that.ds[id]=dataSource;
            });
        },
        remKML:function(id){
            var ds=this.ds[id]
            this.viewer.dataSources.remove(ds);
            delete this.ds[id]
        },
        loadKML:function(item_selected){
            //对比出来当前加载的和重新选择的变化的是哪个
            var add=[]
            var del=[]
            //遍历当前选中，如果哪个不在current中，认为其为新增
            for(var i in item_selected){
                var sId= item_selected[i];
                if(sId){
                    if(!this.item_current.includes(sId)){
                        add.push(sId);
                    }
                }
            }
            //遍历当前显示，如果哪个不在select中，认为其为删除
            for(var i in this.item_current){
                var cId =  this.item_current[i];
                if(cId){
                    if(!item_selected.includes(cId)){
                        del.push(cId);
                    }
                }
            }
            this.item_current=this.item_selected;
            add.forEach((v)=>{
                this.addKML(v)
            })
            del.forEach((v)=>{
                this.remKML(v)
            })
        },

        delFile: function () {
            if (this.file_selected && this.file_selected.length > 0) {
                let ids = []
                for (var i in this.file_selected) {
                    if (this.file_selected[i] && this.file_selected[i].id) {
                        ids = ids.concat(this.file_selected[i].id)
                    }
                }
                let that = this;
                axios.post("/d3/del", ids).then((resp) => {
                    if (resp && resp.data) {
                        let result = resp.data
                        if (result.state == '1') {
                            that.list()
                            return
                        }
                    }
                    alert("删除失败")
                })
            }
        },
        addLayer: function () {
            alert(1)
        },

        uploadFile: function () {
            let formData = new FormData();
            let that = this;
            let onUploadProgress = () => {
                that.list();
                that.file = null
            };

            formData.append("file", this.file);

            return axios.post("/d3/upload", formData, {
                headers: {
                    "Content-Type": "multipart/form-data"
                }, onUploadProgress
            });
        },

        list: function () {
            axios.get("/d3/list").then((resp) => {
                if (resp && resp.data) {
                    let result = resp.data;
                    if (result.code == '200') {
                        let data = result.data;
                        this.files = data;
                    } else {
                        alert('获取文件列表出错:' + result.message)
                    }
                }
            })
            axios.get("/d3/group").then((resp) => {

                if (resp && resp.data) {
                    let result = resp.data;
                    if (result.code == '200') {
                        let data = result.data;
                        this.items = data;
                    } else {
                        alert('获取树形列表出错:' + result.message)
                    }
                }
            })
        }
    },
    watch: {
        item_selected:function(nv,ov){
            this.loadKML(nv)
        }
    },
    mounted: function () {
        this.list()
    },
    components:{
        'KMZ':KMZ
    }
})