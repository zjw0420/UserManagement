import { useEffect, useState, useCallback } from 'react';
import { positionApi } from '../api/position';
import type { SystemPost } from '../types';
import { validate } from '../utils/validate';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm: SystemPost = { postCode: '', name: '', description: '', status: '1' };
const rules = {
  postCode: { label: '编码', required: true },
  name:     { label: '名称', required: true },
} as const;

export default function PositionList() {
  const [list, setList] = useState<SystemPost[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState<SystemPost>(emptyForm);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [editingId, setEditingId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [delId, setDelId] = useState<number | null>(null);

  const load = useCallback(async () => {
    try { const res = await positionApi.list(); setList((res.data ?? []).sort((a, b) => (a.id ?? 0) - (b.id ?? 0))); } catch { /* */ }
  }, []);

  useEffect(() => { load(); }, [load]);

  const openCreate = () => { setEditingId(null); setForm(emptyForm); setErrors({}); setModalOpen(true); };
  const openEdit = async (id: number) => {
    try {
      const res = await positionApi.getById(id);
      const p = res.data!;
      setEditingId(id);
      setForm({ postCode: p.postCode, name: p.name, description: p.description, status: p.status });
      setErrors({});
      setModalOpen(true);
    } catch { /* */ }
  };

  const handleSubmit = async () => {
    const errs = validate(form, rules);
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setSubmitting(true);
    try { await positionApi.save(editingId ? { ...form, id: editingId } : form); setModalOpen(false); load(); }
    catch { /* */ }
    finally { setSubmitting(false); }
  };

  const confirmDelete = (id: number) => { setDelId(id); setConfirmOpen(true); };
  const doDelete = async () => { if (delId) { await positionApi.delete(delId); load(); } setConfirmOpen(false); };

  return (
    <div>
      <h1 className="page-title">岗位管理</h1>
      <div className="toolbar">
        <div style={{ flex: 1 }} />
        <button className="btn btn-primary" onClick={openCreate}>➕ 新增岗位</button>
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>编码</th><th>名称</th><th>描述</th><th>状态</th><th>创建时间</th><th style={{ width: 140 }}>操作</th></tr></thead>
        <tbody>
          {list.length === 0 ? <tr><td colSpan={7} className="empty-cell">暂无数据</td></tr> : list.map((p, i) => (
            <tr key={p.id}>
              <td>{i + 1}</td><td>{p.postCode}</td><td>{p.name}</td><td>{p.description}</td>
              <td><span className={`tag ${p.status === '1' ? 'tag-active' : 'tag-inactive'}`}>{p.status === '1' ? '启用' : '禁用'}</span></td>
              <td>{p.createTime?.slice(0, 10)}</td>
              <td className="action-cell">
                <button className="btn-action" onClick={() => openEdit(p.id!)}>编辑</button>
                <button className="btn-action danger" onClick={() => confirmDelete(p.id!)}>删除</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <Modal open={modalOpen} title={editingId ? '编辑岗位' : '新增岗位'} onClose={() => setModalOpen(false)}>
        <div className="form-grid">
          <div className="form-group">
            <label>编码 <span className="required">*</span></label>
            <input value={form.postCode} onChange={e => setForm({ ...form, postCode: e.target.value })} required />
            {errors.postCode && <span className="form-error">{errors.postCode}</span>}
          </div>
          <div className="form-group">
            <label>名称 <span className="required">*</span></label>
            <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
            {errors.name && <span className="form-error">{errors.name}</span>}
          </div>
          <div className="form-group"><label>描述</label><input value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} /></div>
          <div className="form-group"><label>状态</label><select value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}><option value="1">启用</option><option value="0">禁用</option></select></div>
        </div>
        <div className="modal-footer">
          <button className="btn btn-cancel" onClick={() => setModalOpen(false)}>取消</button>
          <button className="btn btn-primary" onClick={handleSubmit} disabled={submitting}>{submitting ? '保存中...' : '保存'}</button>
        </div>
      </Modal>

      <ConfirmDialog open={confirmOpen} title="确认删除" message="确定要删除该岗位吗？" onConfirm={doDelete} onCancel={() => setConfirmOpen(false)} danger />
    </div>
  );
}
