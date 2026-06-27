import { useEffect, useState, useCallback } from 'react';
import {
  facilityApi, labelApi, leaseTermApi, paymentTypeApi,
  type DictEntity,
} from '../api/dictionary';
import { attrApi, feeApi } from '../api/kvdict';
import { validate } from '../utils/validate';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';

// ========== Simple CRUD tabs (Facility, Label, LeaseTerm, Payment) ==========
const crudDicts = [
  { key: 'facility', label: '设施', api: facilityApi,
    columns: [{ key: 'name', label: '名称' }, { key: 'icon', label: '图标' }],
    emptyForm: { name: '', icon: '' } },
  { key: 'label', label: '标签', api: labelApi,
    columns: [{ key: 'name', label: '名称' }],
    emptyForm: { name: '' } },
  { key: 'leaseTerm', label: '租期', api: leaseTermApi,
    columns: [{ key: 'monthCount', label: '月数' }, { key: 'unit', label: '单位' }],
    emptyForm: { monthCount: 1, unit: '月' } },
  { key: 'paymentType', label: '支付方式', api: paymentTypeApi,
    columns: [{ key: 'name', label: '名称' }, { key: 'payMonthCount', label: '每次支付月数' }],
    emptyForm: { name: '', payMonthCount: '1' } },
];

function CrudTab({ dict }: { dict: typeof crudDicts[0] }) {
  const [list, setList] = useState<DictEntity[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState<DictEntity>(dict.emptyForm as DictEntity);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [editingId, setEditingId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [delId, setDelId] = useState<number | null>(null);

  const load = useCallback(async () => {
    try { const res = await dict.api.list(); setList(res.data as DictEntity[] ?? []); } catch { /* */ }
  }, [dict.api]);

  useEffect(() => { load(); }, [load]);

  const openCreate = () => { setEditingId(null); setForm(dict.emptyForm as DictEntity); setErrors({}); setModalOpen(true); };
  const openEdit = async (id: number) => {
    try { const res = await dict.api.getById(id); setEditingId(id); setForm(res.data as DictEntity ?? dict.emptyForm); setErrors({}); setModalOpen(true); } catch { /* */ }
  };
  const handleSubmit = async () => {
    const errs = validate(form, { name: { label: '名称', required: true } });
    setErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setSubmitting(true);
    try { await dict.api.save(editingId ? { ...form, id: editingId } : form); setModalOpen(false); load(); }
    catch { /* */ } finally { setSubmitting(false); }
  };
  const confirmDelete = (id: number) => { setDelId(id); setConfirmOpen(true); };
  const doDelete = async () => { if (delId) { await dict.api.delete(delId); load(); } setConfirmOpen(false); };

  return (
    <div>
      <div className="toolbar">
        <div style={{ flex: 1 }} />
        <button className="btn btn-primary" onClick={openCreate}>➕ 新增{dict.label}</button>
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th>{dict.columns.map(c => <th key={c.key}>{c.label}</th>)}<th style={{ width: 140 }}>操作</th></tr></thead>
        <tbody>
          {list.length === 0 ? <tr><td colSpan={2 + dict.columns.length} className="empty-cell">暂无数据</td></tr>
            : list.map(row => (
              <tr key={row.id}>
                <td>{row.id}</td>
                {dict.columns.map(c => <td key={c.key}>{row[c.key] != null ? String(row[c.key]) : '-'}</td>)}
                <td className="action-cell">
                  <button className="btn-action" onClick={() => openEdit(row.id!)}>编辑</button>
                  <button className="btn-action danger" onClick={() => confirmDelete(row.id!)}>删除</button>
                </td>
              </tr>
            ))}
        </tbody>
      </table>

      <Modal open={modalOpen} title={(editingId ? '编辑' : '新增') + dict.label} onClose={() => setModalOpen(false)}>
        <div className="form-grid">
          {dict.columns.map(c => (
            <div className="form-group" key={c.key}>
              <label>{c.label}</label>
              <input value={form[c.key] != null ? String(form[c.key]) : ''}
                onChange={e => setForm({ ...form, [c.key]: e.target.value })} required={c.key === 'name'} />
              {c.key === 'name' && errors.name && <span className="form-error">{errors.name}</span>}
            </div>
          ))}
        </div>
        <div className="modal-footer">
          <button className="btn btn-cancel" onClick={() => setModalOpen(false)}>取消</button>
          <button className="btn btn-primary" onClick={handleSubmit} disabled={submitting}>{submitting ? '保存中...' : '保存'}</button>
        </div>
      </Modal>
      <ConfirmDialog open={confirmOpen} title="确认删除" message={`确定要删除该${dict.label}吗？`} onConfirm={doDelete} onCancel={() => setConfirmOpen(false)} danger />
    </div>
  );
}

// ========== Key-Value tab (Attr / Fee) ==========
interface KVItem { id?: number; name: string; attrKeyId?: number; feeKeyId?: number; unit?: string; }

function KVTab({ label, api, valueName }: { label: string; api: typeof attrApi; valueName: string }) {
  const [keys, setKeys] = useState<KVItem[]>([]);
  const [selectedKeyId, setSelectedKeyId] = useState<number | null>(null);
  const [values, setValues] = useState<KVItem[]>([]);

  // Key states
  const [keyModalOpen, setKeyModalOpen] = useState(false);
  const [keyForm, setKeyForm] = useState({ name: '' });
  const [keySubmitting, setKeySubmitting] = useState(false);
  const [keyDelId, setKeyDelId] = useState<number | null>(null);
  const [keyConfirmOpen, setKeyConfirmOpen] = useState(false);

  // Value states
  const [valModalOpen, setValModalOpen] = useState(false);
  const [valForm, setValForm] = useState<KVItem>({ name: '' });
  const [valSubmitting, setValSubmitting] = useState(false);
  const [valDelId, setValDelId] = useState<number | null>(null);
  const [valConfirmOpen, setValConfirmOpen] = useState(false);

  const loadKeys = useCallback(async () => {
    try { const res = await api.keyList(); setKeys(res.data as KVItem[] ?? []); } catch { /* */ }
  }, [api]);
  const loadValues = useCallback(async (keyId: number) => {
    try { const res = await api.valueList(keyId); setValues(res.data as KVItem[] ?? []); } catch { /* */ }
  }, [api]);

  useEffect(() => { loadKeys(); }, [loadKeys]);
  useEffect(() => { if (selectedKeyId) loadValues(selectedKeyId); }, [selectedKeyId, loadValues]);

  // Key handlers
  const openKeyCreate = () => { setKeyForm({ name: '' }); setKeyModalOpen(true); };
  const handleKeySubmit = async () => {
    const errs = validate(keyForm as unknown as Record<string,unknown>, { name: { label: '名称', required: true } });
    if (Object.keys(errs).length > 0) { alert(errs.name); return; }
    setKeySubmitting(true);
    try { await api.keySave(keyForm as KVItem & { id?: number }); setKeyModalOpen(false); loadKeys(); }
    catch { /* */ } finally { setKeySubmitting(false); }
  };
  const confirmKeyDelete = (id: number) => { setKeyDelId(id); setKeyConfirmOpen(true); };
  const doKeyDelete = async () => {
    if (keyDelId) {
      await api.keyDelete(keyDelId);
      if (selectedKeyId === keyDelId) { setSelectedKeyId(null); setValues([]); }
      loadKeys();
    }
    setKeyConfirmOpen(false);
  };

  // Value handlers
  const openValCreate = () => { setValForm({ name: '' }); setValModalOpen(true); };
  const handleValSubmit = async () => {
    if (!selectedKeyId) return;
    const errs = validate(valForm as unknown as Record<string,unknown>, { name: { label: '名称', required: true } });
    if (Object.keys(errs).length > 0) { alert(errs.name); return; }
    setValSubmitting(true);
    try {
      const entity = valueName === '属性值'
        ? { ...valForm, attrKeyId: selectedKeyId }
        : { ...valForm, feeKeyId: selectedKeyId };
      await api.valueSave(entity as KVItem & { id?: number });
      setValModalOpen(false); loadValues(selectedKeyId);
    } catch { /* */ } finally { setValSubmitting(false); }
  };
  const confirmValDelete = (id: number) => { setValDelId(id); setValConfirmOpen(true); };
  const doValDelete = async () => {
    if (valDelId) { await api.valueDelete(valDelId); loadValues(selectedKeyId!); }
    setValConfirmOpen(false);
  };

  return (
    <div className="kv-layout" style={{ display: 'flex', gap: 20 }}>
      {/* Key panel */}
      <div style={{ width: 250, flexShrink: 0 }}>
        <div className="toolbar" style={{ marginBottom: 8 }}>
          <strong>{label}分类</strong>
          <button className="btn btn-primary" style={{ padding: '2px 10px', fontSize: 12 }} onClick={openKeyCreate}>+</button>
        </div>
        <div style={{ maxHeight: 400, overflowY: 'auto' }}>
          {keys.length === 0 ? <p className="empty-cell">暂无{label}分类</p>
            : keys.map(k => (
              <div key={k.id} style={{
                padding: '6px 10px', cursor: 'pointer', borderRadius: 4, marginBottom: 2,
                background: selectedKeyId === k.id ? 'var(--primary)' : '#f5f5f5',
                color: selectedKeyId === k.id ? '#fff' : 'var(--gray-800)',
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
              }} onClick={() => setSelectedKeyId(k.id!)}>
                <span>{k.name}</span>
                <span style={{ cursor: 'pointer', fontSize: 11, opacity: 0.7 }} onClick={e => { e.stopPropagation(); confirmKeyDelete(k.id!); }}>✕</span>
              </div>
            ))}
        </div>
      </div>

      {/* Value panel */}
      <div style={{ flex: 1 }}>
        <div className="toolbar">
          <strong>{selectedKeyId ? keys.find(k => k.id === selectedKeyId)?.name + ' — ' + valueName : '请先选择分类'}</strong>
          {selectedKeyId && <button className="btn btn-primary" onClick={openValCreate}>➕ 新增{valueName}</button>}
        </div>
        {selectedKeyId && (
          <table className="data-table">
            <thead><tr><th>ID</th><th>{valueName}</th><th>单位</th><th style={{ width: 80 }}>操作</th></tr></thead>
            <tbody>
              {values.length === 0 ? <tr><td colSpan={4} className="empty-cell">暂无{valueName}</td></tr>
                : values.map(v => (
                  <tr key={v.id}>
                    <td>{v.id}</td><td>{v.name}</td><td>{v.unit ?? '-'}</td>
                    <td><button className="btn-action danger" onClick={() => confirmValDelete(v.id!)}>删除</button></td>
                  </tr>
                ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Key modal */}
      <Modal open={keyModalOpen} title={`新增${label}分类`} onClose={() => setKeyModalOpen(false)} width={400}>
        <div className="form-group"><label>名称</label><input value={keyForm.name} onChange={e => setKeyForm({ name: e.target.value })} autoFocus /></div>
        <div className="modal-footer">
          <button className="btn btn-cancel" onClick={() => setKeyModalOpen(false)}>取消</button>
          <button className="btn btn-primary" onClick={handleKeySubmit} disabled={keySubmitting}>保存</button>
        </div>
      </Modal>
      <ConfirmDialog open={keyConfirmOpen} title="确认删除" message="删除分类会清空所有关联值" onConfirm={doKeyDelete} onCancel={() => setKeyConfirmOpen(false)} danger />

      {/* Value modal */}
      <Modal open={valModalOpen} title={`新增${valueName}`} onClose={() => setValModalOpen(false)} width={400}>
        <div className="form-group"><label>{valueName}名称</label><input value={valForm.name ?? ''} onChange={e => setValForm({ ...valForm, name: e.target.value })} autoFocus /></div>
        {valueName === '费用值' && <div className="form-group"><label>单位</label><input value={valForm.unit ?? ''} onChange={e => setValForm({ ...valForm, unit: e.target.value })} /></div>}
        <div className="modal-footer">
          <button className="btn btn-cancel" onClick={() => setValModalOpen(false)}>取消</button>
          <button className="btn btn-primary" onClick={handleValSubmit} disabled={valSubmitting}>保存</button>
        </div>
      </Modal>
      <ConfirmDialog open={valConfirmOpen} title="确认删除" message="确定要删除吗？" onConfirm={doValDelete} onCancel={() => setValConfirmOpen(false)} danger />
    </div>
  );
}

// ========== Main Dictionary Page ==========
const tabs = [...crudDicts.map((d, i) => ({ key: d.key, label: d.label, type: 'crud' as const, idx: i })),
  { key: 'attr', label: '属性', type: 'kv' as const },
  { key: 'fee', label: '杂费', type: 'kv' as const },
];

export default function Dictionary() {
  const [active, setActive] = useState(0);
  const tab = tabs[active];

  return (
    <div>
      <h1 className="page-title">字典管理</h1>
      <div style={{ display: 'flex', gap: 4, marginBottom: 16 }}>
        {tabs.map((t, i) => (
          <button key={t.key} className={`tab-btn ${i === active ? 'active' : ''}`}
            style={{ padding: '6px 16px', border: '1px solid var(--gray-300)', borderRadius: 4, background: i === active ? 'var(--primary)' : '#fff', color: i === active ? '#fff' : 'var(--gray-700)', cursor: 'pointer' }}
            onClick={() => setActive(i)}>{t.label}</button>
        ))}
      </div>

      {tab.type === 'crud' ? <CrudTab dict={crudDicts[(tab as typeof tabs[0] & { idx: number }).idx]} /> : null}
      {tab.type === 'kv' && tab.key === 'attr' ? <KVTab label="属性" api={attrApi} valueName="属性值" /> : null}
      {tab.type === 'kv' && tab.key === 'fee' ? <KVTab label="杂费" api={feeApi} valueName="费用值" /> : null}
    </div>
  );
}
