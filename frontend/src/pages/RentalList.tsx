import { useEffect, useState, useCallback } from 'react';
import { rentalApi } from '../api/rental';
import type { AgreementVo, AgreementSubmitVo } from '../types';
import { validate, PHONE_RULE } from '../utils/validate';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm: AgreementSubmitVo = {
  name: '', phone: '', identificationNumber: '', apartmentId: 0, roomId: undefined,
  leaseStartDate: '', leaseEndDate: '', rent: 0, deposit: 0, status: 1, additionalInfo: '',
};
const rentalRules = {
  name:        { label: '承租人', required: true },
  phone:       { label: '手机号', ...PHONE_RULE },
  apartmentId: { label: '公寓ID', required: true },
} as const;

const statusMap: Record<number, string> = {
  1: '签约待确认', 2: '已签约', 3: '已取消',
  4: '已到期', 5: '退租待确认', 6: '已退租', 7: '续约待确认',
};

export default function RentalList() {
  const [list, setList] = useState<AgreementVo[]>([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [keyword, setKeyword] = useState('');
  const pageSize = 10;

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState<AgreementSubmitVo>(emptyForm);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [editingId, setEditingId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [delId, setDelId] = useState<number | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await rentalApi.page(pageNum, pageSize, { name: keyword || undefined });
      const data = res.data;
      setList((data?.records ?? []).sort((a, b) => a.id - b.id));
      setTotal(data?.total ?? 0);
    } catch { /* */ }
  }, [pageNum, keyword]);

  useEffect(() => { load(); }, [load]);

  const openCreate = () => { setEditingId(null); setForm(emptyForm); setErrors({}); setModalOpen(true); };
  const openEdit = async (id: number) => {
    try {
      const res = await rentalApi.getDetailById(id);
      const r = res.data!;
      setEditingId(id);
      setForm({
        name: r.name, phone: r.phone, identificationNumber: r.identificationNumber ?? '',
        apartmentId: r.apartmentId, roomId: r.roomId,
        leaseStartDate: r.leaseStartDate, leaseEndDate: r.leaseEndDate,
        rent: r.rent, deposit: r.deposit, status: r.status, additionalInfo: r.additionalInfo ?? '',
      });
      setErrors({});
      setModalOpen(true);
    } catch { /* */ }
  };

  const handleSubmit = async () => {
    const errs = validate(form, rentalRules);
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setSubmitting(true);
    try { await rentalApi.save(editingId ? { ...form, id: editingId } : form); setModalOpen(false); load(); }
    catch { /* */ } finally { setSubmitting(false); }
  };

  const confirmDelete = (id: number) => { setDelId(id); setConfirmOpen(true); };
  const doDelete = async () => { if (delId) { await rentalApi.delete(delId); load(); } setConfirmOpen(false); };

  const totalPages = Math.ceil(total / pageSize);

  return (
    <div>
      <h1 className="page-title">租赁管理</h1>
      <div className="toolbar">
        <input className="search-input" placeholder="🔍 搜索承租人姓名/电话..." value={keyword} onChange={e => { setKeyword(e.target.value); setPageNum(1); }} />
        <button className="btn btn-primary" onClick={openCreate}>➕ 新增合同</button>
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>承租人</th><th>电话</th><th>公寓ID</th><th>房间号</th><th>起租</th><th>截止</th><th>月租</th><th>押金</th><th>状态</th><th style={{ width: 140 }}>操作</th></tr></thead>
        <tbody>
          {list.length === 0 ? <tr><td colSpan={11} className="empty-cell">暂无数据</td></tr> : list.map((r, i) => (
            <tr key={r.id}>
              <td>{i + 1}</td><td>{r.name}</td><td>{r.phone}</td><td>{r.apartmentId}</td>
              <td>{r.roomNumber ?? r.roomId ?? '-'}</td>
              <td>{r.leaseStartDate}</td><td>{r.leaseEndDate}</td>
              <td>{r.rent}</td><td>{r.deposit}</td>
              <td><span className={`tag ${r.status === 2 ? 'tag-active' : [3,4,6].includes(r.status) ? 'tag-inactive' : 'tag-warn'}`}>{statusMap[r.status] ?? r.status}</span></td>
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

      <Modal open={modalOpen} title={editingId ? '编辑合同' : '新增合同'} onClose={() => setModalOpen(false)} width={600}>
        <div className="form-grid">
          <div className="form-group">
            <label>承租人 <span className="required">*</span></label>
            <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
            {errors.name && <span className="form-error">{errors.name}</span>}
          </div>
          <div className="form-group">
            <label>手机号</label>
            <input value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} placeholder="例如 13800138000" />
            {errors.phone && <span className="form-error">{errors.phone}</span>}
          </div>
          <div className="form-group"><label>身份证号</label><input value={form.identificationNumber ?? ''} onChange={e => setForm({ ...form, identificationNumber: e.target.value })} /></div>
          <div className="form-group">
            <label>公寓ID <span className="required">*</span></label>
            <input type="number" min={1} value={form.apartmentId} onChange={e => setForm({ ...form, apartmentId: Number(e.target.value) })} required />
            {errors.apartmentId && <span className="form-error">{errors.apartmentId}</span>}
          </div>
          <div className="form-group"><label>房间ID</label><input type="number" value={form.roomId ?? ''} onChange={e => setForm({ ...form, roomId: Number(e.target.value) || undefined })} /></div>
          <div className="form-group"><label>起租日期</label><input type="date" value={form.leaseStartDate} onChange={e => setForm({ ...form, leaseStartDate: e.target.value })} /></div>
          <div className="form-group"><label>截止日期</label><input type="date" value={form.leaseEndDate} onChange={e => setForm({ ...form, leaseEndDate: e.target.value })} /></div>
          <div className="form-group"><label>月租(元)</label><input type="number" step="0.01" min={0} value={form.rent} onChange={e => setForm({ ...form, rent: Number(e.target.value) })} /></div>
          <div className="form-group"><label>押金(元)</label><input type="number" step="0.01" min={0} value={form.deposit} onChange={e => setForm({ ...form, deposit: Number(e.target.value) })} /></div>
          <div className="form-group"><label>状态</label><select value={form.status} onChange={e => setForm({ ...form, status: Number(e.target.value) })}>
            <option value={1}>签约待确认</option><option value={2}>已签约</option><option value={3}>已取消</option>
            <option value={4}>已到期</option><option value={5}>退租待确认</option><option value={6}>已退租</option><option value={7}>续约待确认</option>
          </select></div>
          <div className="form-group"><label>备注</label><input value={form.additionalInfo ?? ''} onChange={e => setForm({ ...form, additionalInfo: e.target.value })} /></div>
        </div>
        <div className="modal-footer">
          <button className="btn btn-cancel" onClick={() => setModalOpen(false)}>取消</button>
          <button className="btn btn-primary" onClick={handleSubmit} disabled={submitting}>{submitting ? '保存中...' : '保存'}</button>
        </div>
      </Modal>

      <ConfirmDialog open={confirmOpen} title="确认删除" message="确定要删除该租赁合同吗？" onConfirm={doDelete} onCancel={() => setConfirmOpen(false)} danger />
    </div>
  );
}
