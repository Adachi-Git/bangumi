import{d as I,r as $,o as U,c as i,a as t,t as v,F as w,b as k,e as h,w as u,n as V,f as l}from"./index-QA_4Gfa8.js";const A={class:"blog",id:"blog"},D={class:"blog-title"},F={class:"blog-info"},O={class:"blog-author"},j={class:"blog-time"},q={key:0,class:"blog-relative"},P={class:"blog-relative-subject"},z={class:"tip"},G={class:"relative"},K=["src"],Q={class:"title"},W=["innerHTML"],X={key:1,class:"blog-tag"},Y=t("div",{class:"tip"},"标签：",-1),Z={class:"blog-tag-item"},tt={key:2,class:"divider"},et={key:3,class:"blog-comment"},st={class:"blog-comment-title"},ot=t("div",{class:"title"},"精选留言",-1),nt=t("div",{style:{flex:"1"}},null,-1),it={class:"blog-comment-item"},lt=["src","onClick"],ct={class:"comment-content"},at={class:"info"},dt=["onClick"],rt={class:"time"},_t=["innerHTML","onClick"],vt=t("div",{style:{height:"12px"}},null,-1),gt={class:"blog-comment-item"},ut=["src","onClick"],ht={class:"comment-content"},pt={class:"info"},wt=["onClick"],kt={class:"time"},ft=["innerHTML","onClick"],mt={key:4,class:"blog-space"},Ct=I({__name:"BlogView",setup(yt){const n=$(),m=$(),J={loadBlogDetail:async c=>{n.value=c,await V();const a=m.value;if(a){const s=a.querySelectorAll("img"),e=[];for(let _=0;_<s.length;_++){const d=s[_],g=d.src;g!=null&&g.length>0&&g.indexOf("/img/smiles")==-1&&(e.push(g),d.onclick=()=>{window.android&&window.android.onPreviewImage(g,e)})}}}},E=c=>{const a=(c||"").trim(),s="　　",e=s+a.replace(/&nbsp;/g," ").trim().replace(/(\r\n|\n|\r)\s+/g,"$1").replace(/(\r\n|\n|\r)/g,`$1${s}`);return console.log(e),e},y=(c,a,s)=>{b(c,s==null);const e=(s==null?void 0:s.replyJs)||"",_=a.replyJs||"",d=e.length>0?e:_;if(window.android){if(d.length>0){window.android.onReplyUser(e.length>0?e:d,JSON.stringify(s||a));return}window.android.onNeedLogin()}},S=c=>{b(c,!0),window.android&&window.android.onReplyNew()},p=c=>{window.android&&window.android.onClickUser(c.userId)},b=(c,a)=>{const s=document.getElementById("blog"),e=c.target;if(e&&s){const _=e.getBoundingClientRect();s.scrollTo({top:s.scrollTop+_.top-(a?40:60),behavior:"smooth"});const d=()=>{e.classList.remove("blinking"),e.removeEventListener("animationend",d)};e.classList.add("blinking"),e.addEventListener("animationend",d)}};return U(()=>{window.blog=J,window.mounted=!0}),(c,a)=>{var s,e,_,d,g,C,L,N,T,M,R,x,B,H;return l(),i("div",A,[t("div",D,v((s=n.value)==null?void 0:s.title),1),t("div",F,[t("div",O,v((e=n.value)==null?void 0:e.userName),1),t("div",j,v((_=n.value)==null?void 0:_.time),1)]),(((d=n.value)==null?void 0:d.related)||[]).length>0?(l(),i("div",q,[t("div",P,[t("div",z,"关联的条目 "+v((C=(g=n.value)==null?void 0:g.related)==null?void 0:C.length)+" 个",1),(l(!0),i(w,null,k(((L=n.value)==null?void 0:L.related)||[],o=>(l(),i("div",G,[t("img",{src:o.cover,alt:"img"},null,8,K),t("div",Q,"# "+v(o.titleNative),1)]))),256))])])):h("",!0),t("div",{class:"blog-content",ref_key:"blogContentRef",ref:m,innerHTML:E((N=n.value)==null?void 0:N.content)},null,8,W),(((T=n.value)==null?void 0:T.tags)||[]).length>0?(l(),i("div",X,[Y,(l(!0),i(w,null,k(((M=n.value)==null?void 0:M.tags)||[],o=>(l(),i("div",Z,v(o.title),1))),256))])):h("",!0),(R=n.value)!=null&&R.content?(l(),i("hr",tt)):h("",!0),(x=n.value)!=null&&x.content?(l(),i("div",et,[t("div",st,[ot,nt,t("div",{class:"write",onClick:a[0]||(a[0]=u(o=>S(o),["stop"]))},"写留言")]),(l(!0),i(w,null,k(((B=n.value)==null?void 0:B.comments)||[],o=>(l(),i("div",it,[t("img",{class:"avatar",src:o.userAvatar,alt:"img",onClick:u(r=>p(o),["stop"])},null,8,lt),t("div",ct,[t("div",at,[t("div",{class:"user-name",onClick:u(r=>p(o),["stop"])},v(o.userName),9,dt),t("div",rt,v(o.time),1)]),t("div",{class:"blog-html",innerHTML:o.replyContent,onClick:u(r=>y(r,o,null),["stop"])},null,8,_t),vt,(l(!0),i(w,null,k(o.topicSubReply||[],r=>(l(),i("div",gt,[t("img",{class:"avatar sub",src:r.userAvatar,alt:"img",onClick:u(f=>p(r),["stop"])},null,8,ut),t("div",ht,[t("div",pt,[t("div",{class:"user-name",onClick:u(f=>p(r),["stop"])},v(r.userName),9,wt),t("div",kt,v(r.time),1)]),t("div",{class:"blog-html",innerHTML:r.replyContent,onClick:u(f=>y(f,o,r),["stop"])},null,8,ft)])]))),256))])]))),256))])):h("",!0),(H=n.value)!=null&&H.content?(l(),i("div",mt," 我是有底线的 ")):h("",!0)])}}});export{Ct as default};
