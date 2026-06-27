import { useEffect, useState, useCallback } from 'react';
import { roomApi } from '../api/room';
import type { RoomItemVo, RoomSubmitVo } from '../types';
import { validate } from '../utils/validate';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm: RoomSubmitVo = {
  roomNumber: '', rent: 0, apartmentId: 0, isRelease: 1,
  facilityInfoIds: [], labelIds: [], attrValueIds: [], leaseTermIds: [], paymentTypeIds: [],
};
const roomRules = {
  roomNumber:  { label: '房间号', required: true },
  rent:        { label: '月租金', required: true },
  apartmentId: { label: '所属公寓', required: true },
} as const;

export default function RoomList() {
  const [list, setList] = useState<RoomItemVo[]>([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [keyword, setKeyword] = useState('');
  const pageSize = 10;

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState<RoomSubmitVo>(emptyForm);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [editingId, setEditingId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [delId, setDelId] = useState<number | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await roomApi.page(pageNum, pageSize, { roomNumber: keyword || undefined });
      const data = res.data;
      setList((data?.records ?? []).sort((a, b) => a.id - b.id));
      setTotal(data?.total ?? 0);
    } catch { /* */ }
  }, [pageNum, keyword]);

  useEffect(() => { load(); }, [load]);

  const openCreate = () => { setEditingId(null); setForm(emptyForm); setErrors({}); setModalOpen(true); };
  const openEdit = async (id: number) => {
    try {
      const res = await roomApi.getDetailById(id);
      const r = res.data!;
      setEditingId(id);
      setForm({
        roomNumber: r.roomNumber, rent: r.rent, apartmentId: r.apartmentId, isRelease: r.isRelease,
        facilityInfoIds: r.facilityInfoList?.map(f => f.id) ?? [],
        labelIds: r.labelInfoList?.map(l => l.id) ?? [],
        attrValueIds: r.attrValueList?.map(a => a.id) ?? [],
        leaseTermIds: r.leaseTermList?.map(l => l.id) ?? [],
        paymentTypeIds: r.paymentTypeList?.map(p => p.id) ?? [],
      });
      setErrors({});
      setModalOpen(true);
    } catch { /* */ }
  };

  const handleSubmit = async () => {
    const errs = validate(form, roomRules);
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setSubmitting(true);
    try { await roomApi.save(editingId ? { ...form, id: editingId } : form); setModalOpen(false); load(); }
    catch { /* */ } finally { setSubmitting(false); }
  };

  const confirmDelete = (id: number) => { setDelId(id); setConfirmOpen(true); };
  const doDelete = async () => { if (delId) { await roomApi.delete(delId); load(); } setConfirmOpen(false); };

  const totalPages = Math.ceil(total / pageSize);

  return (
    <div>
      <h1 className="page-title">房间管理</h1>
      <div className="toolbar">
        <input className="search-input" placeholder="🔍 搜索房间号..." value={keyword} onChange={e => { setKeyword(e.target.value); setPageNum(1); }} />
        <button className="btn btn-primary" onClick={openCreate}>➕ 新增房间</button>
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>房间号</th><th>月租金</th><th>所属公寓</th><th>状态</th><th style={{ width: 140 }}>操作</th></tr></thead>
        <tbody>
          {list.length === 0 ? <tr><td colSpan={6} className="empty-cell">暂无数据</td></tr> : list.map((r, i) => (
            <tr key={r.id}>
              <td>{i + 1}</td><td>{r.roomNumber}</td><td>{r.rent}</td>
              <td>{r.apartmentName ?? r.apartmentId}</td>
              <td><span className={`tag ${r.isRelease === 1 ? 'tag-active' : 'tag-inactive'}`}>{r.isRelease === 1 ? '已发布' : '未发布'}</span></td>
              <td className="action-cell">
                <button className="btn-action" onClick={() => openEdit(r.id)}>编辑</button>
                <button className="btn-action danger" onClick={() => confirmDelete(r.id)}>删除</button>
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

      <Modal open={modalOpen} title={editingId ? '编辑房间' : '新增房间'} onClose={() => setModalOpen(false)}>
        <div className="form-grid">
          <div className="form-group">
            <label>房间号 <span className="required">*</span></label>
            <input value={form.roomNumber} onChange={e => setForm({ ...form, roomNumber: e.target.value })} required />
            {errors.roomNumber && <span className="form-error">{errors.roomNumber}</span>}
          </div>
          <div className="form-group">
            <label>月租金 <span className="required">*</span></label>
            <input type="number" step="0.01" min={0} value={form.rent} onChange={e => setForm({ ...form, rent: Number(e.target.value) })} required />
            {errors.rent && <span className="form-error">{errors.rent}</span>}
          </div>
          <div className="form-group">
            <label>所属公寓ID <span className="required">*</span></label>
            <input type="number" min={1} value={form.apartmentId} onChange={e => setForm({ ...form, apartmentId: Number(e.target.value) })} required />
            {errors.apartmentId && <span className="form-error">{errors.apartmentId}</span>}
          </div>
          <div className="form-group"><label>状态</label><select value={form.isRelease} onChange={e => setForm({ ...form, isRelease: Number(e.target.value) })}><option value={1}>已发布</option><option value={0}>未发布</option></select></div>
        </div>
        <div className="modal-footer">
          <button className="btn btn-cancel" onClick={() => setModalOpen(false)}>取消</button>
          <button className="btn btn-primary" onClick={handleSubmit} disabled={submitting}>{submitting ? '保存中...' : '保存'}</button>
        </div>
      </Modal>

      <ConfirmDialog open={confirmOpen} title="确认删除" message="确定要删除该房间吗？" onConfirm={doDelete} onCancel={() => setConfirmOpen(false)} danger />
    </div>
  );
}
