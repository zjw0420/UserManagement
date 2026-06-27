import { useEffect, useState, useCallback } from 'react';
import { userApi } from '../api/user';
import type { SystemUserItemVo, SystemUserInfoVo } from '../types';
import { validate, PHONE_RULE, PASSWORD_RULE, USERNAME_RULE } from '../utils/validate';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm: SystemUserInfoVo = {
  username: '', password: '', name: '', type: 0, phone: '', status: 1, postId: undefined,
};
const typeMap: Record<number, string> = { 0: '普通用户', 1: '管理员' };
const statusMap: Record<number, string> = { 1: '正常', 0: '禁用' };

// ℹ️ 校验规则：字段名对表单字段，label 用于错误提示
const userRules = {
  username: { label: '用户名', required: true, ...USERNAME_RULE },
  password: { label: '密码', required: true, ...PASSWORD_RULE },
  name:    { label: '姓名', required: true },
  phone:   { label: '手机号', ...PHONE_RULE },
} as const;

export default function UserList() {
  const [users, setUsers] = useState<SystemUserItemVo[]>([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [keyword, setKeyword] = useState('');
  const [type, setType] = useState<number | undefined>();
  const pageSize = 10;

  const [modalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState<SystemUserInfoVo>(emptyForm);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [editingId, setEditingId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [confirmMsg, setConfirmMsg] = useState('');
  const [confirmAction, setConfirmAction] = useState<() => void>(() => {});

  const load = useCallback(async () => {
    try {
      const res = await userApi.page(pageNum, pageSize, { keyword, type });
      const data = res.data;
      setUsers((data?.records ?? []).sort((a, b) => a.id - b.id));
      setTotal(data?.total ?? 0);
    } catch { /* 全局拦截器已弹窗 */ }
  }, [pageNum, keyword, type]);

  useEffect(() => { load(); }, [load]);

  const openCreate = () => { setEditingId(null); setFormData(emptyForm); setErrors({}); setModalOpen(true); };
  const openEdit = async (id: number) => {
    try {
      const res = await userApi.getById(id);
      const u = res.data!;
      setEditingId(id);
      setFormData({ username: u.username, name: u.name, type: u.type, phone: u.phone, status: u.status, postId: u.postId });
      setErrors({});
      setModalOpen(true);
    } catch { /* */ }
  };

  const handleSubmit = async () => {
    // ℹ️ 校验（编辑时不校验密码）
    const rules = editingId ? { ...userRules, password: undefined } : userRules;
    const errs = validate(formData, rules);
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setSubmitting(true);
    try {
      editingId ? await userApi.update({ ...formData, id: editingId }) : await userApi.create(formData);
      setModalOpen(false); load();
    } catch { /* 全局拦截器已弹窗 */ }
    finally { setSubmitting(false); }
  };

  const confirmDelete = (id: number) => {
    setConfirmMsg('确定要删除该用户吗？');
    setConfirmAction(() => () => { userApi.delete(id).then(load); setConfirmOpen(false); });
    setConfirmOpen(true);
  };

  const confirmBatchDelete = () => {
    if (selectedIds.size === 0) return;
    setConfirmMsg(`确定要删除选中的 ${selectedIds.size} 个用户吗？`);
    setConfirmAction(() => () => {
      Promise.all([...selectedIds].map(id => userApi.delete(id))).then(() => { setSelectedIds(new Set()); load(); });
      setConfirmOpen(false);
    });
    setConfirmOpen(true);
  };

  const toggleSelect = (id: number) => setSelectedIds(prev => { const n = new Set(prev); n.has(id) ? n.delete(id) : n.add(id); return n; });
  const toggleAll = () => setSelectedIds(selectedIds.size === users.length ? new Set() : new Set(users.map(u => u.id)));

  const totalPages = Math.ceil(total / pageSize);

  return (
    <div>
      <h1 className="page-title">用户管理</h1>
      <div className="toolbar">
        <div className="toolbar-left">
          <input className="search-input" placeholder="🔍 搜索用户名/姓名..." value={keyword} onChange={e => { setKeyword(e.target.value); setPageNum(1); }} />
          <select value={type ?? ''} onChange={e => { setType(e.target.value ? Number(e.target.value) : undefined); setPageNum(1); }}>
            <option value="">全部角色</option><option value={1}>管理员</option><option value={0}>普通用户</option>
          </select>
        </div>
        <div className="toolbar-right">
          {selectedIds.size > 0 && <button className="btn btn-danger-outline" onClick={confirmBatchDelete}>删除选中({selectedIds.size})</button>}
          <button className="btn btn-primary" onClick={openCreate}>➕ 新增用户</button>
        </div>
      </div>

      <table className="data-table">
        <thead><tr>
          <th style={{ width: 40 }}><input type="checkbox" checked={users.length > 0 && selectedIds.size === users.length} onChange={toggleAll} /></th>
          <th>ID</th><th>用户名</th><th>姓名</th><th>角色</th><th>手机号</th><th>岗位</th><th>状态</th><th>创建时间</th><th style={{ width: 140 }}>操作</th>
        </tr></thead>
        <tbody>
          {users.length === 0 ? <tr><td colSpan={10} className="empty-cell">暂无数据</td></tr> : users.map((u, i) => (
            <tr key={u.id} className={selectedIds.has(u.id) ? 'selected-row' : ''}>
              <td><input type="checkbox" checked={selectedIds.has(u.id)} onChange={() => toggleSelect(u.id)} /></td>
              <td>{i + 1}</td><td>{u.username}</td><td>{u.name}</td>
              <td><span className={`tag ${u.type === 1 ? 'tag-admin' : 'tag-user'}`}>{typeMap[u.type] ?? u.type}</span></td>
              <td>{u.phone}</td><td>{u.postName ?? '-'}</td>
              <td><span className={`tag ${u.status === 1 ? 'tag-active' : 'tag-inactive'}`}>{statusMap[u.status] ?? u.status}</span></td>
              <td>{u.createTime?.slice(0, 10)}</td>
              <td className="action-cell">
                <button className="btn-action" onClick={() => openEdit(u.id)}>编辑</button>
                <button className="btn-action danger" onClick={() => confirmDelete(u.id)}>删除</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {totalPages > 1 && (
        <div className="pagination">
          <button disabled={pageNum <= 1} onClick={() => setPageNum(pageNum - 1)}>上一页</button>
          <span>{pageNum} / {totalPages}</span>
          <button disabled={pageNum >= totalPages} onClick={() => setPageNum(pageNum + 1)}>下一页</button>
        </div>
      )}

      <Modal open={modalOpen} title={editingId ? '编辑用户' : '新增用户'} onClose={() => setModalOpen(false)}>
        <div className="form-grid">
          <div className="form-group">
            <label>用户名 <span className="required">*</span></label>
            <input value={formData.username} onChange={e => setFormData({ ...formData, username: e.target.value })} disabled={!!editingId} required minLength={2} maxLength={20} />
            {errors.username && <span className="form-error">{errors.username}</span>}
          </div>
          {!editingId && <div className="form-group">
            <label>密码 <span className="required">*</span></label>
            <input type="password" value={formData.password ?? ''} onChange={e => setFormData({ ...formData, password: e.target.value })} required minLength={8} />
            {errors.password && <span className="form-error">{errors.password}</span>}
          </div>}
          <div className="form-group">
            <label>姓名</label>
            <input value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} required />
            {errors.name && <span className="form-error">{errors.name}</span>}
          </div>
          <div className="form-group"><label>角色</label><select value={formData.type} onChange={e => setFormData({ ...formData, type: Number(e.target.value) })}><option value={0}>普通用户</option><option value={1}>管理员</option></select></div>
          <div className="form-group">
            <label>手机号</label>
            <input value={formData.phone} onChange={e => setFormData({ ...formData, phone: e.target.value })} placeholder="例如 13800138000" pattern="1[3-9]\d{9}" />
            {errors.phone && <span className="form-error">{errors.phone}</span>}
          </div>
          <div className="form-group"><label>岗位ID</label><input type="number" value={formData.postId ?? ''} onChange={e => setFormData({ ...formData, postId: Number(e.target.value) || undefined })} /></div>
          <div className="form-group"><label>状态</label><select value={formData.status} onChange={e => setFormData({ ...formData, status: Number(e.target.value) })}><option value={1}>正常</option><option value={0}>禁用</option></select></div>
        </div>
        <div className="modal-footer">
          <button className="btn btn-cancel" onClick={() => setModalOpen(false)}>取消</button>
          <button className="btn btn-primary" onClick={handleSubmit} disabled={submitting}>{submitting ? '保存中...' : '保存'}</button>
        </div>
      </Modal>

      <ConfirmDialog open={confirmOpen} title="确认操作" message={confirmMsg} onConfirm={confirmAction} onCancel={() => setConfirmOpen(false)} danger />
    </div>
  );
}
