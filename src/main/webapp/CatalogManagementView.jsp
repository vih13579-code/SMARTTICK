<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Catalog Management | SMARTTICK</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/fonts/themify-icons/themify-icons.css">
</head>
<body class="dashboard-page admin-ops-page">
<div class="dashboard-shell">
    <jsp:include page="SidebarDashboard.jsp"/>
    <main class="dash-main">

        <%-- â•â•â• Page header â•â•â• --%>
        <div class="dash-hero">
            <div class="dash-title">
                <h1>Catalog Management</h1>
                <p>Manage product categories and brands. Items currently used by products cannot be deleted.</p>
            </div>
        </div>

        <%-- â•â•â• Alerts â•â•â• --%>
        <c:if test="${not empty sessionScope.success}">
            <div class="alert alert-success" style="border-radius:14px;margin-bottom:18px">
                <c:out value="${sessionScope.success}"/>
            </div>
            <c:remove var="success" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.error}">
            <div class="alert alert-danger" style="border-radius:14px;margin-bottom:18px">
                <c:out value="${sessionScope.error}"/>
            </div>
            <c:remove var="error" scope="session"/>
        </c:if>

        <%-- â•â•â• Tabs â•â•â• --%>
        <div class="dash-card">
            <div class="catalog-tabs" id="catalogTabs">
                <button class="catalog-tab active" data-tab="category" type="button">
                    <i class="ti-layout-grid2"></i>&nbsp; Category
                    <span style="margin-left:6px;padding:2px 9px;border-radius:8px;background:rgba(255,255,255,.08);font-size:12px">
                        ${categories.size()}
                    </span>
                </button>
                <button class="catalog-tab" data-tab="brand" type="button">
                    <i class="ti-tag"></i>&nbsp; Brand
                    <span style="margin-left:6px;padding:2px 9px;border-radius:8px;background:rgba(255,255,255,.08);font-size:12px">
                        ${brands.size()}
                    </span>
                </button>
            </div>

            <%-- â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
                 TAB 1: CATEGORIES (Category)
                 â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â• --%>
            <div class="catalog-tab-panel active" id="panel-category">
                <%-- Toolbar --%>
                <div class="catalog-toolbar">
                    <div class="catalog-search">
                        <i class="ti-search"></i>
                        <input type="text" id="searchCategory" placeholder="Search categories..." autocomplete="off">
                    </div>
                    <button class="btn-add" type="button" onclick="openModal('create','category')">
                        <i class="ti-plus"></i> Add Category
                    </button>
                </div>

                <%-- Data table --%>
                <div class="table-scroll">
                    <table class="catalog-table" id="tableCat">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Category Name</th>
                                <th style="text-align:center">Products</th>
                                <th>Created Date</th>
                                <th>Status</th>
                                <th style="text-align:right">Actions</th>
                            </tr>
                        </thead>
                        <tbody id="bodyCat">
                            <c:forEach items="${categories}" var="c" varStatus="st">
                                <tr data-name="${c.name}">
                                    <td class="col-id">${c.categoryId}</td>
                                    <td style="font-weight:700"><c:out value="${c.name}"/></td>
                                    <td class="col-count">
                                        <span>${c.productCount}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.createdDate != null}">
                                                <fmt:formatDate value="${c.createdDate}" pattern="dd/MM/yyyy"/>
                                            </c:when>
                                            <c:otherwise>â€”</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/catalog" style="display:inline">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="type" value="category">
                                            <input type="hidden" name="id" value="${c.categoryId}">
                                            <input type="hidden" name="status" value="${c.status ? '0' : '1'}">
                                            <button type="submit"
                                                    class="status-pill-cat ${c.status ? 'active' : 'hidden'}"
                                                    title="Click to ${c.status ? 'hide' : 'show'}">
                                                <span class="status-dot"></span>
                                                ${c.status ? 'Active' : 'Hidden'}
                                            </button>
                                        </form>
                                    </td>
                                    <td class="col-actions">
                                        <button class="action-btn action-edit" type="button" title="Edit"
                                                onclick="openModal('update','category','${c.categoryId}','<c:out value="${c.name}"/>')">
                                            <i class="ti-pencil"></i>
                                        </button>
                                        <button class="action-btn action-delete" type="button" title="Delete"
                                                onclick="openDelete('category','${c.categoryId}','<c:out value="${c.name}"/>',${c.productCount})">
                                            <i class="ti-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <%-- Empty state --%>
                <c:if test="${empty categories}">
                    <div class="catalog-empty">
                        <i class="ti-layout-grid2"></i>
                        <p>No categories yet. Click <strong>+ Add Category</strong> to get started.</p>
                    </div>
                </c:if>

                <%-- Pagination --%>
                <div class="catalog-pagination" id="paginationCat"></div>
            </div>

            <%-- â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
                 TAB 2: BRANDS (Brand)
                 â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â• --%>
            <div class="catalog-tab-panel" id="panel-brand">
                <%-- Toolbar --%>
                <div class="catalog-toolbar">
                    <div class="catalog-search">
                        <i class="ti-search"></i>
                        <input type="text" id="searchBrand" placeholder="Search brands..." autocomplete="off">
                    </div>
                    <button class="btn-add" type="button" onclick="openModal('create','brand')">
                        <i class="ti-plus"></i> Add Brand
                    </button>
                </div>

                <%-- Data table --%>
                <div class="table-scroll">
                    <table class="catalog-table" id="tableBrand">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Brand Name</th>
                                <th style="text-align:center">Products</th>
                                <th>Created Date</th>
                                <th>Status</th>
                                <th style="text-align:right">Actions</th>
                            </tr>
                        </thead>
                        <tbody id="bodyBrand">
                            <c:forEach items="${brands}" var="b">
                                <tr data-name="${b.name}">
                                    <td class="col-id">${b.brandId}</td>
                                    <td style="font-weight:700"><c:out value="${b.name}"/></td>
                                    <td class="col-count">
                                        <span>${b.productCount}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${b.createdDate != null}">
                                                <fmt:formatDate value="${b.createdDate}" pattern="dd/MM/yyyy"/>
                                            </c:when>
                                            <c:otherwise>â€”</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/catalog" style="display:inline">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="type" value="brand">
                                            <input type="hidden" name="id" value="${b.brandId}">
                                            <input type="hidden" name="status" value="${b.status ? '0' : '1'}">
                                            <button type="submit"
                                                    class="status-pill-cat ${b.status ? 'active' : 'hidden'}"
                                                    title="Click to ${b.status ? 'hide' : 'show'}">
                                                <span class="status-dot"></span>
                                                ${b.status ? 'Active' : 'Hidden'}
                                            </button>
                                        </form>
                                    </td>
                                    <td class="col-actions">
                                        <button class="action-btn action-edit" type="button" title="Edit"
                                                onclick="openModal('update','brand','${b.brandId}','<c:out value="${b.name}"/>')">
                                            <i class="ti-pencil"></i>
                                        </button>
                                        <button class="action-btn action-delete" type="button" title="Delete"
                                                onclick="openDelete('brand','${b.brandId}','<c:out value="${b.name}"/>',${b.productCount})">
                                            <i class="ti-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <c:if test="${empty brands}">
                    <div class="catalog-empty">
                        <i class="ti-tag"></i>
                        <p>No brands yet. Click <strong>+ Add Brand</strong> to get started.</p>
                    </div>
                </c:if>

                <div class="catalog-pagination" id="paginationBrand"></div>
            </div>
        </div><%-- /dash-card --%>

        <%-- â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
             MODAL: Create / Update
             â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â• --%>
        <div class="modal-overlay" id="modalCrud">
            <div class="modal-card">
                <div class="modal-header">
                    <h3 id="modalTitle">Add Category</h3>
                    <button class="modal-close" type="button" onclick="closeModal('modalCrud')">&times;</button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/admin/catalog" id="formCrud">
                    <input type="hidden" name="action" id="fAction">
                    <input type="hidden" name="type" id="fType">
                    <input type="hidden" name="id" id="fId">
                    <div class="modal-body">
                        <label for="fName" id="fLabel">Category Name</label>
                        <input type="text" name="name" id="fName" required autocomplete="off" placeholder="Enter name...">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" onclick="closeModal('modalCrud')">Cancel</button>
                        <button type="submit" class="btn-save" id="fSubmit">Save</button>
                    </div>
                </form>
            </div>
        </div>

        <%-- â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
             MODAL: Delete Confirm
             â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â• --%>
        <div class="modal-overlay" id="modalDelete">
            <div class="modal-card">
                <div class="modal-header">
                    <h3>Confirm Delete</h3>
                    <button class="modal-close" type="button" onclick="closeModal('modalDelete')">&times;</button>
                </div>
                <div class="confirm-icon"><i class="ti-alert"></i></div>
                <p class="confirm-text">
                    Are you sure you want to delete this?
                    <strong id="deleteName"></strong>
                </p>
                <div class="confirm-warning" id="deleteWarning" style="display:none"></div>
                <form method="post" action="${pageContext.request.contextPath}/admin/catalog" id="formDelete">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="type" id="dType">
                    <input type="hidden" name="id" id="dId">
                    <div class="modal-footer" style="justify-content:center">
                        <button type="button" class="btn-cancel" onclick="closeModal('modalDelete')">Cancel</button>
                        <button type="submit" class="btn-delete-confirm" id="dSubmit">Delete</button>
                    </div>
                </form>
            </div>
        </div>

    </main>
</div>

<script>
/* â”€â”€ Tabs â”€â”€ */
(function(){
    const tabs = document.querySelectorAll('.catalog-tab');
    const panels = document.querySelectorAll('.catalog-tab-panel');
    const params = new URLSearchParams(window.location.search);
    const init = params.get('tab') || 'category';

    function activate(name){
        tabs.forEach(t => t.classList.toggle('active', t.dataset.tab === name));
        panels.forEach(p => p.classList.toggle('active', p.id === 'panel-' + name));
    }
    activate(init);
    tabs.forEach(t => t.addEventListener('click', () => activate(t.dataset.tab)));
})();

/* â”€â”€ Search (client-side filter) â”€â”€ */
function bindSearch(inputId, tbodyId, paginationId) {
    const input = document.getElementById(inputId);
    const tbody = document.getElementById(tbodyId);
    if (!input || !tbody) return;
    input.addEventListener('input', function(){
        const q = this.value.trim().toLowerCase();
        const rows = tbody.querySelectorAll('tr');
        rows.forEach(r => {
            const name = (r.dataset.name || '').toLowerCase();
            r.style.display = name.includes(q) ? '' : 'none';
        });
        paginate(tbodyId, paginationId);
    });
}
bindSearch('searchCategory', 'bodyCat', 'paginationCat');
bindSearch('searchBrand', 'bodyBrand', 'paginationBrand');

/* â”€â”€ Pagination â”€â”€ */
const PAGE_SIZE = 10;
function paginate(tbodyId, paginationId, page) {
    const tbody = document.getElementById(tbodyId);
    const pag = document.getElementById(paginationId);
    if (!tbody || !pag) return;
    const rows = Array.from(tbody.querySelectorAll('tr')).filter(r => r.style.display !== 'none');
    const total = rows.length;
    const pages = Math.max(1, Math.ceil(total / PAGE_SIZE));
    page = Math.max(1, Math.min(page || 1, pages));

    rows.forEach((r, i) => {
        r.style.display = (i >= (page-1)*PAGE_SIZE && i < page*PAGE_SIZE) ? '' : 'none';
    });

    /* Re-show search-hidden rows: we rely on data-vis attribute */
    const q = (tbodyId === 'bodyCat' ?
        document.getElementById('searchCategory') :
        document.getElementById('searchBrand')).value.trim().toLowerCase();

    Array.from(tbody.querySelectorAll('tr')).forEach((r, i) => {
        const nameMatch = (r.dataset.name || '').toLowerCase().includes(q);
        if (!nameMatch) { r.style.display = 'none'; return; }
        const idx = rows.indexOf(r);
        r.style.display = (idx >= (page-1)*PAGE_SIZE && idx < page*PAGE_SIZE) ? '' : 'none';
    });

    const from = total === 0 ? 0 : (page-1)*PAGE_SIZE + 1;
    const to = Math.min(page*PAGE_SIZE, total);

    let html = '<span class="page-info">Showing ' + from + 'â€“' + to + ' / ' + total + '</span>';
    if (pages > 1) {
        html += '<div class="page-btns">';
        html += '<button class="page-btn" onclick="paginate(\''+tbodyId+'\',\''+paginationId+'\','+(page-1)+')" '+(page===1?'disabled':'')+'>â€¹</button>';
        for (let i = 1; i <= pages; i++) {
            html += '<button class="page-btn'+(i===page?' active':'')+'" onclick="paginate(\''+tbodyId+'\',\''+paginationId+'\','+i+')">'+i+'</button>';
        }
        html += '<button class="page-btn" onclick="paginate(\''+tbodyId+'\',\''+paginationId+'\','+(page+1)+')" '+(page===pages?'disabled':'')+'>â€º</button>';
        html += '</div>';
    }
    pag.innerHTML = html;
}
paginate('bodyCat', 'paginationCat', 1);
paginate('bodyBrand', 'paginationBrand', 1);

/* â”€â”€ Modal: Create / Update â”€â”€ */
function openModal(action, type, id, name) {
    const isCategory = type === 'category';
    const isCreate = action === 'create';
    document.getElementById('modalTitle').textContent =
        isCreate ? ('Add ' + (isCategory ? 'category' : 'brand'))
                 : ('Edit ' + (isCategory ? 'category' : 'brand'));
    document.getElementById('fLabel').textContent = isCategory ? 'Category Name' : 'Brand Name';
    document.getElementById('fAction').value = action;
    document.getElementById('fType').value = type;
    document.getElementById('fId').value = id || '';
    document.getElementById('fName').value = name || '';
    document.getElementById('fName').placeholder = 'Enter ' + (isCategory ? 'category' : 'brand') + '...';
    document.getElementById('fSubmit').textContent = isCreate ? 'Add' : 'Save';
    showOverlay('modalCrud');
    setTimeout(() => document.getElementById('fName').focus(), 200);
}

/* â”€â”€ Modal: Delete Confirm â”€â”€ */
function openDelete(type, id, name, count) {
    document.getElementById('deleteName').textContent = name;
    document.getElementById('dType').value = type;
    document.getElementById('dId').value = id;
    const warn = document.getElementById('deleteWarning');
    const submit = document.getElementById('dSubmit');
    if (count > 0) {
        warn.style.display = 'block';
        warn.textContent = 'This item has ' + count + ' linked products and cannot be deleted.';
        submit.disabled = true;
        submit.style.opacity = '.4';
        submit.style.cursor = 'not-allowed';
    } else {
        warn.style.display = 'none';
        submit.disabled = false;
        submit.style.opacity = '1';
        submit.style.cursor = 'pointer';
    }
    showOverlay('modalDelete');
}

/* â”€â”€ Overlay helpers â”€â”€ */
function showOverlay(id) {
    document.getElementById(id).classList.add('show');
    document.body.style.overflow = 'hidden';
}
function closeModal(id) {
    document.getElementById(id).classList.remove('show');
    document.body.style.overflow = '';
}
/* Close on backdrop click */
document.querySelectorAll('.modal-overlay').forEach(o => {
    o.addEventListener('click', e => { if (e.target === o) closeModal(o.id); });
});
/* Close on Escape */
document.addEventListener('keydown', e => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay.show').forEach(o => closeModal(o.id));
    }
});

/* â”€â”€ Auto-dismiss alerts â”€â”€ */
document.querySelectorAll('.alert').forEach(el => {
    setTimeout(() => { el.style.transition = 'opacity .4s'; el.style.opacity = '0'; setTimeout(() => el.remove(), 400); }, 4000);
});
</script>
</body>
</html>

